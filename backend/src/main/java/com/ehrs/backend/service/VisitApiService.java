package com.ehrs.backend.service;

import com.ehrs.backend.dto.AssignDoctorRequest;
import com.ehrs.backend.dto.CreateVisitRequest;
import com.ehrs.backend.dto.CreateVisitResponse;
import com.ehrs.backend.dto.QueueEntryDto;
import com.ehrs.backend.dto.UpdateVisitSummaryRequest;
import com.ehrs.backend.model.AppUser;
import com.ehrs.backend.model.Doctor;
import com.ehrs.backend.model.Nurse;
import com.ehrs.backend.model.Patient;
import com.ehrs.backend.model.Visit;
import com.ehrs.backend.model.VisitStatusHistory;
import com.ehrs.backend.repository.AppUserRepository;
import com.ehrs.backend.repository.DoctorRepository;
import com.ehrs.backend.repository.NurseRepository;
import com.ehrs.backend.repository.PatientRepository;
import com.ehrs.backend.repository.VisitRepository;
import com.ehrs.backend.repository.VisitStatusHistoryRepository;
import javax.persistence.EntityNotFoundException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class VisitApiService {

    private static final DateTimeFormatter DATE_TIME_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    private static final DateTimeFormatter REQUEST_DATE_TIME_FORMAT = DateTimeFormatter.ISO_LOCAL_DATE_TIME;

    private final VisitRepository visitRepository;
    private final PatientRepository patientRepository;
    private final DoctorRepository doctorRepository;
    private final NurseRepository nurseRepository;
    private final AppUserRepository appUserRepository;
    private final VisitStatusHistoryRepository visitStatusHistoryRepository;
    private final AuthApiService authApiService;

    public VisitApiService(
            VisitRepository visitRepository,
            PatientRepository patientRepository,
            DoctorRepository doctorRepository,
            NurseRepository nurseRepository,
            AppUserRepository appUserRepository,
            VisitStatusHistoryRepository visitStatusHistoryRepository,
            AuthApiService authApiService
    ) {
        this.visitRepository = visitRepository;
        this.patientRepository = patientRepository;
        this.doctorRepository = doctorRepository;
        this.nurseRepository = nurseRepository;
        this.appUserRepository = appUserRepository;
        this.visitStatusHistoryRepository = visitStatusHistoryRepository;
        this.authApiService = authApiService;
    }

    @Transactional
    public CreateVisitResponse createVisit(CreateVisitRequest request, String authorizationHeader) {
        Patient patient = patientRepository.findById(UUID.fromString(request.getPatientid()))
                .orElseThrow(() -> new EntityNotFoundException("Patient not found"));

        AppUser createdBy = authApiService.resolveCurrentUser(authorizationHeader)
                .orElseGet(() -> appUserRepository.findFirstByStatusOrderByCreatedAtAsc("ACTIVE")
                        .orElseThrow(() -> new EntityNotFoundException("No active application user found")));

        Doctor doctor = authApiService.resolveCurrentUser(authorizationHeader)
                .flatMap(user -> doctorRepository.findFirstByUser_UserIdAndStatus(user.getUserId(), "ACTIVE"))
                .orElse(null);
        Nurse nurse = authApiService.resolveCurrentUser(authorizationHeader)
                .flatMap(user -> nurseRepository.findFirstByUser_UserIdAndStatus(user.getUserId(), "ACTIVE"))
                .orElseGet(() -> nurseRepository.findFirstByStatusOrderByNurseIdAsc("ACTIVE").orElse(null));

        Visit visit = new Visit();
        visit.setVisitId(UUID.randomUUID());
        visit.setPatient(patient);
        visit.setAssignedDoctor(doctor);
        visit.setAssignedNurse(nurse);
        visit.setCreatedByUser(createdBy);
        visit.setVisitDate(resolveVisitDate(request.getVisit_date()));
        visit.setCurrentStatus(normalizeStatus(request.getStatus()));
        visit.setVisitReason(request.getVisit_reason());
        visit.setCreatedAt(LocalDateTime.now());
        visit.setUpdatedAt(LocalDateTime.now());

        visitRepository.save(visit);

        VisitStatusHistory history = new VisitStatusHistory();
        history.setVisitStatusHistoryId(UUID.randomUUID());
        history.setVisit(visit);
        history.setOldStatus(null);
        history.setNewStatus(visit.getCurrentStatus());
        history.setChangedByUser(createdBy);
        history.setChangeReason("Initial visit creation");
        history.setChangedAt(LocalDateTime.now());
        visitStatusHistoryRepository.save(history);

        return new CreateVisitResponse(visit.getVisitId().toString(), visit.getCurrentStatus(), "Visit created");
    }

    @Transactional(readOnly = true)
    public List<QueueEntryDto> getQueue() {
        return visitRepository.findAllByOrderByVisitDateDesc().stream()
                .map(this::toQueueEntry)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<QueueEntryDto> getQueueByStatus(String status) {
        return visitRepository.findByCurrentStatusIgnoreCaseOrderByVisitDateDesc(normalizeStatus(status)).stream()
                .map(this::toQueueEntry)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<QueueEntryDto> getDoctorQueue(String authorizationHeader) {
        Optional<Doctor> currentDoctor = authApiService.resolveCurrentUser(authorizationHeader)
                .flatMap(user -> doctorRepository.findFirstByUser_UserIdAndStatus(user.getUserId(), "ACTIVE"));

        if (currentDoctor.isPresent()) {
            return visitRepository.findByAssignedDoctor_DoctorIdAndCurrentStatusIgnoreCaseOrderByVisitDateDesc(
                            currentDoctor.get().getDoctorId(),
                            "Waiting for Doctor")
                    .stream()
                    .map(this::toQueueEntry)
                    .collect(Collectors.toList());
        }

        return getQueueByStatus("Waiting for Doctor");
    }

    @Transactional
    public CreateVisitResponse assignDoctor(AssignDoctorRequest request, String authorizationHeader) {
        Visit visit = visitRepository.findById(UUID.fromString(request.getVisitid()))
                .orElseThrow(() -> new EntityNotFoundException("Visit not found"));
        Doctor doctor = doctorRepository.findByEmployeeNumber(request.getEmployeeid())
                .orElseThrow(() -> new EntityNotFoundException("Doctor not found"));
        String previousStatus = visit.getCurrentStatus();

        visit.setAssignedDoctor(doctor);
        visit.setCurrentStatus("Waiting for Doctor");
        visit.setUpdatedAt(LocalDateTime.now());
        visitRepository.save(visit);

        AppUser changedBy = authApiService.resolveCurrentUser(authorizationHeader)
                .orElseGet(() -> visit.getAssignedNurse() != null ? visit.getAssignedNurse().getUser() : visit.getCreatedByUser());

        VisitStatusHistory history = new VisitStatusHistory();
        history.setVisitStatusHistoryId(UUID.randomUUID());
        history.setVisit(visit);
        history.setOldStatus(previousStatus);
        history.setNewStatus(visit.getCurrentStatus());
        history.setChangedByUser(changedBy);
        history.setChangeReason("Doctor assigned to visit");
        history.setChangedAt(LocalDateTime.now());
        visitStatusHistoryRepository.save(history);

        return new CreateVisitResponse(visit.getVisitId().toString(), visit.getCurrentStatus(), "Doctor assigned");
    }

    @Transactional
    public CreateVisitResponse updateVisitSummary(UpdateVisitSummaryRequest request, String authorizationHeader) {
        Visit visit = resolveVisitForSummaryUpdate(request);
        String previousStatus = visit.getCurrentStatus();
        visit.setVisitSummary(request.getVisit_summary());
        visit.setCurrentStatus(normalizeStatus(request.getNewStatus()));
        visit.setUpdatedAt(LocalDateTime.now());
        visitRepository.save(visit);

        AppUser changedBy = authApiService.resolveCurrentUser(authorizationHeader)
                .orElseGet(() -> visit.getAssignedDoctor() != null ? visit.getAssignedDoctor().getUser() : visit.getCreatedByUser());

        VisitStatusHistory history = new VisitStatusHistory();
        history.setVisitStatusHistoryId(UUID.randomUUID());
        history.setVisit(visit);
        history.setOldStatus(previousStatus);
        history.setNewStatus(visit.getCurrentStatus());
        history.setChangedByUser(changedBy);
        history.setChangeReason("Visit summary updated");
        history.setChangedAt(LocalDateTime.now());
        visitStatusHistoryRepository.save(history);

        return new CreateVisitResponse(visit.getVisitId().toString(), visit.getCurrentStatus(), "Visit summary updated");
    }

    private Visit resolveVisitForSummaryUpdate(UpdateVisitSummaryRequest request) {
        if (request.getVisit_id() != null && !request.getVisit_id().isBlank()) {
            return visitRepository.findById(UUID.fromString(request.getVisit_id()))
                    .orElseThrow(() -> new EntityNotFoundException("Visit not found"));
        }

        if (request.getDoctor_id() != null && !request.getDoctor_id().isBlank()
                && request.getStatus() != null && !request.getStatus().isBlank()) {
            List<Visit> candidates = visitRepository.findByAssignedDoctor_DoctorIdAndCurrentStatusIgnoreCaseOrderByVisitDateDesc(
                    UUID.fromString(request.getDoctor_id()), normalizeStatus(request.getStatus()));

            if (request.getPatientid() != null && !request.getPatientid().isBlank()) {
                UUID patientId = UUID.fromString(request.getPatientid());
                return candidates.stream()
                        .filter(visit -> visit.getPatient().getPatientId().equals(patientId))
                        .findFirst()
                        .orElseThrow(() -> new EntityNotFoundException("No matching visit found for doctor, patient, and status"));
            }

            if (candidates.size() == 1) {
                return candidates.get(0);
            }

            if (candidates.isEmpty()) {
                throw new EntityNotFoundException("No matching visit found for doctor and status");
            }

            throw new IllegalArgumentException("Multiple visits match this doctor and status; provide visit_id or patientid");
        }

        throw new EntityNotFoundException("Insufficient information to locate visit");
    }

    private QueueEntryDto toQueueEntry(Visit visit) {
        String nurseName = visit.getAssignedNurse() == null
                ? ""
                : visit.getAssignedNurse().getUser().getFirstName() + " " + visit.getAssignedNurse().getUser().getLastName();
        String doctorName = visit.getAssignedDoctor() == null
                ? ""
                : visit.getAssignedDoctor().getUser().getFirstName() + " " + visit.getAssignedDoctor().getUser().getLastName();

        return new QueueEntryDto(
                visit.getVisitId().toString(),
                visit.getPatient().getPatientId().toString(),
                visit.getPatient().getFirstName() + " " + visit.getPatient().getLastName(),
                visit.getVisitDate().format(DATE_TIME_FORMAT),
                nurseName,
                doctorName,
                visit.getCurrentStatus()
        );
    }

    private String normalizeStatus(String value) {
        String normalized = value.toLowerCase(Locale.ROOT);
        if ("waiting for triage".equals(normalized)) {
            return "Waiting for Triage";
        }
        if ("waiting for doctor".equals(normalized)) {
            return "Waiting for Doctor";
        }
        if ("waiting for payment".equals(normalized)) {
            return "Waiting for Payment";
        }
        if ("waiting for checkout".equals(normalized) || "ready for checkout".equals(normalized)) {
            return "Ready for Checkout";
        }
        return value;
    }

    private LocalDateTime resolveVisitDate(String requestedVisitDate) {
        if (requestedVisitDate == null || requestedVisitDate.isBlank()) {
            return LocalDateTime.now();
        }
        return LocalDateTime.parse(requestedVisitDate, REQUEST_DATE_TIME_FORMAT);
    }
}
