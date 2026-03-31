package com.ehrs.backend.service;

import com.ehrs.backend.dto.AuthUserDto;
import com.ehrs.backend.dto.LoginRequest;
import com.ehrs.backend.dto.LoginResponse;
import com.ehrs.backend.model.AppUser;
import com.ehrs.backend.model.Doctor;
import com.ehrs.backend.model.Nurse;
import com.ehrs.backend.repository.AppUserRepository;
import com.ehrs.backend.repository.DoctorRepository;
import com.ehrs.backend.repository.NurseRepository;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.UUID;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
public class AuthApiService {

    private final AppUserRepository appUserRepository;
    private final DoctorRepository doctorRepository;
    private final NurseRepository nurseRepository;
    private final PasswordHashService passwordHashService;
    private final AuthTokenService authTokenService;

    public AuthApiService(
            AppUserRepository appUserRepository,
            DoctorRepository doctorRepository,
            NurseRepository nurseRepository,
            PasswordHashService passwordHashService,
            AuthTokenService authTokenService
    ) {
        this.appUserRepository = appUserRepository;
        this.doctorRepository = doctorRepository;
        this.nurseRepository = nurseRepository;
        this.passwordHashService = passwordHashService;
        this.authTokenService = authTokenService;
    }

    @Transactional
    public LoginResponse login(LoginRequest request) {
        AppUser user = appUserRepository.findByUsernameIgnoreCase(request.getUsername().trim())
                .filter(candidate -> "ACTIVE".equalsIgnoreCase(candidate.getStatus()))
                .orElseThrow(() -> invalidCredentials());

        if (!passwordHashService.matches(request.getPassword(), user.getPasswordHash())) {
            throw invalidCredentials();
        }

        user.setLastLoginAt(LocalDateTime.now());
        appUserRepository.save(user);

        return new LoginResponse(authTokenService.createToken(user.getUserId()), toAuthUser(user));
    }

    @Transactional(readOnly = true)
    public AuthUserDto getCurrentUser(String authorizationHeader) {
        return resolveCurrentUser(authorizationHeader)
                .map(this::toAuthUser)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Authentication required"));
    }

    @Transactional(readOnly = true)
    public Optional<AppUser> resolveCurrentUser(String authorizationHeader) {
        return extractBearerToken(authorizationHeader)
                .flatMap(authTokenService::parseUserId)
                .flatMap(userId -> appUserRepository.findByUserIdAndStatus(userId, "ACTIVE"));
    }

    @Transactional(readOnly = true)
    public Optional<AuthUserDto> resolveCurrentUserProfile(String authorizationHeader) {
        return resolveCurrentUser(authorizationHeader).map(this::toAuthUser);
    }

    private AuthUserDto toAuthUser(AppUser user) {
        List<String> roles = appUserRepository.findRoleCodesByUserId(user.getUserId());
        String primaryRole = resolvePrimaryRole(roles);
        Optional<Doctor> doctor = doctorRepository.findFirstByUser_UserIdAndStatus(user.getUserId(), "ACTIVE");
        Optional<Nurse> nurse = nurseRepository.findFirstByUser_UserIdAndStatus(user.getUserId(), "ACTIVE");

        return new AuthUserDto(
                user.getUserId().toString(),
                user.getUsername(),
                user.getFirstName(),
                user.getLastName(),
                user.getFirstName() + " " + user.getLastName(),
                roles,
                primaryRole,
                resolveHomeRoute(primaryRole),
                doctor.map(Doctor::getEmployeeNumber).orElse(null),
                nurse.map(candidate -> candidate.getNurseId().toString()).orElse(null),
                nurse.map(Nurse::getEmployeeNumber).orElse(null)
        );
    }

    private Optional<String> extractBearerToken(String authorizationHeader) {
        if (authorizationHeader == null) {
            return Optional.empty();
        }

        String prefix = "Bearer ";
        if (!authorizationHeader.regionMatches(true, 0, prefix, 0, prefix.length())) {
            return Optional.empty();
        }

        String token = authorizationHeader.substring(prefix.length()).trim();
        return token.isEmpty() ? Optional.empty() : Optional.of(token);
    }

    private String resolvePrimaryRole(List<String> roles) {
        List<String> roleCodes = roles == null ? Collections.emptyList() : roles;
        String[] priority = {"ADMIN", "DOCTOR", "NURSE", "INTAKE", "CHECKOUT"};

        for (String roleCode : priority) {
            if (roleCodes.stream().anyMatch(candidate -> roleCode.equalsIgnoreCase(candidate))) {
                return roleCode;
            }
        }

        return roleCodes.isEmpty() ? "USER" : roleCodes.get(0).toUpperCase(Locale.ROOT);
    }

    private String resolveHomeRoute(String primaryRole) {
        if ("DOCTOR".equals(primaryRole)) {
            return "/home/doctorlanding";
        }
        if ("NURSE".equals(primaryRole)) {
            return "/home/nurselanding";
        }
        if ("CHECKOUT".equals(primaryRole)) {
            return "/home/checkout";
        }
        return "/home/visitation-queue";
    }

    private ResponseStatusException invalidCredentials() {
        return new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid username or password");
    }
}
