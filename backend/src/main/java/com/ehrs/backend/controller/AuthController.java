package com.ehrs.backend.controller;

import com.ehrs.backend.dto.AuthUserDto;
import com.ehrs.backend.dto.LoginRequest;
import com.ehrs.backend.dto.LoginResponse;
import com.ehrs.backend.service.AuthApiService;
import javax.validation.Valid;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class AuthController {

    private final AuthApiService authApiService;

    public AuthController(AuthApiService authApiService) {
        this.authApiService = authApiService;
    }

    @PostMapping("/api/v1/auth/login")
    public LoginResponse login(@Valid @RequestBody LoginRequest request) {
        return authApiService.login(request);
    }

    @GetMapping("/api/v1/auth/me")
    public AuthUserDto getCurrentUser(@RequestHeader(value = "Authorization", required = false) String authorizationHeader) {
        return authApiService.getCurrentUser(authorizationHeader);
    }
}
