package com.ehrs.backend.service;

import com.ehrs.backend.dto.DiagnosisListItemDto;
import com.ehrs.backend.dto.MedicationReferenceDto;
import com.ehrs.backend.dto.MedicalRecordItemDto;
import com.ehrs.backend.dto.OrderRequest;
import com.ehrs.backend.dto.OrderResponse;
import com.ehrs.backend.dto.PatientVitalsDto;
import com.ehrs.backend.dto.SavePatientVitalsRequest;
import com.ehrs.backend.dto.SaveVisitMedicationRequest;
import com.ehrs.backend.dto.VisitMedicationDto;
import com.ehrs.backend.dto.VisitHistoryItemDto;
import com.ehrs.backend.model.AppUser;
import com.ehrs.backend.model.Doctor;
import com.ehrs.backend.model.MedicationReference;
import com.ehrs.backend.model.MedicalRecordSection;
import com.ehrs.backend.model.Nurse;
import com.ehrs.backend.model.OrderItem;
import com.ehrs.backend.model.Patient;
import com.ehrs.backend.model.Visit;
import com.ehrs.backend.model.VisitMedication;
import com.ehrs.backend.model.VisitVital;
import com.ehrs.backend.repository.DiagnosisReferenceRepository;
import com.ehrs.backend.repository.DoctorRepository;
import com.ehrs.backend.repository.MedicationReferenceRepository;
import com.ehrs.backend.repository.MedicalRecordSectionRepository;
import com.ehrs.backend.repository.NurseRepository;
import com.ehrs.backend.repository.OrderItemRepository;
import com.ehrs.backend.repository.PatientRepository;
import com.ehrs.backend.repository.VisitRepository;
import com.ehrs.backend.repository.VisitMedicationRepository;
import com.ehrs.backend.repository.VisitVitalRepository;
import javax.persistence.EntityNotFoundException;
import org.springframework.http.HttpStatus;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
public class ConsultationApiService {

    private static final DateTimeFormatter DATE_TIME_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    private final DiagnosisReferenceRepository diagnosisReferenceRepository;
    private final MedicalRecordSectionRepository medicalRecordSectionRepository;
    private final VisitRepository visitRepository;
    private final DoctorRepository doctorRepository;
    private final NurseRepository nurseRepository;
    private final MedicationReferenceRepository medicationReferenceRepository;
    private final VisitMedicationRepository visitMedicationRepository;
    private final PatientRepository patientRepository;
    private final OrderItemRepository orderItemRepository;
    private final VisitVitalRepository visitVitalRepository;
    private final AuthApiService authApiService;

    public ConsultationApiService(
            DiagnosisReferenceRepository diagnosisReferenceRepository,
            MedicalRecordSectionRepository medicalRecordSectionRepository,
            VisitRepository visitRepository,
            DoctorRepository doctorRepository,
            NurseRepository nurseRepository,
            MedicationReferenceRepository medicationReferenceRepository,
            VisitMedicationRepository visitMedicationRepository,
            PatientRepository patientRepository,
            OrderItemRepository orderItemRepository,
            VisitVitalRepository visitVitalRepository,
            AuthApiService authApiService
    ) {
        this.diagnosisReferenceRepository = diagnosisReferenceRepository;
        this.medicalRecordSectionRepository = medicalRecordSectionRepository;
        this.visitRepository = visitRepository;
        this.doctorRepository = doctorRepository;
        this.nurseRepository = nurseRepository;
        this.medicationReferenceRepository = medicationReferenceRepository;
        this.visitMedicationRepository = visitMedicationRepository;
        this.patientRepository = patientRepository;
        this.orderItemRepository = orderItemRepository;
        this.visitVitalRepository = visitVitalRepository;
        this.authApiService = authApiService;
    }

    @Transactional(readOnly = true)
    public List<DiagnosisListItemDto> getDiagnosisList() {
        return diagnosisReferenceRepository.findByStatusIgnoreCaseOrderByDiagnosisCodeAsc("ACTIVE").stream()
                .map(item -> new DiagnosisListItemDto(item.getDiagnosisCode(), item.getDiagnosisName()))
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<VisitHistoryItemDto> getVisitationHistory(String patientId) {
        return visitRepository.findByPatient_PatientIdOrderByVisitDateDesc(UUID.fromString(patientId)).stream()
                .map(visit -> new VisitHistoryItemDto(
                        visit.getVisitId().toString(),
                        visit.getVisitDate().format(DATE_TIME_FORMAT),
                        visit.getVisitReason(),
                        visit.getSymptoms(),
                        visit.getVisitSummary(),
                        visit.getCurrentStatus()))
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<MedicalRecordItemDto> getMedicalRecord(String patientId) {
        return medicalRecordSectionRepository.findByPatient_PatientIdOrderByRecordedAtDesc(UUID.fromString(patientId)).stream()
                .map(this::toMedicalRecordItem)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public PatientVitalsDto getPatientVitals(String patientId, String visitId) {
        UUID patientUuid = UUID.fromString(patientId);
        patientRepository.findById(patientUuid)
                .orElseThrow(() -> new EntityNotFoundException("Patient not found"));

        if (visitId != null && !visitId.isBlank()) {
            Visit visit = resolveVisitForPatient(visitId, patientUuid);
            return visitVitalRepository.findFirstByVisit_VisitIdOrderByRecordedAtDesc(visit.getVisitId())
                    .map(this::toPatientVitals)
                    .orElse(emptyVitals(patientId, visitId));
        }

        return visitVitalRepository
                .findFirstByPatient_PatientIdOrderByRecordedAtDesc(patientUuid)
                .map(this::toPatientVitals)
                .orElse(emptyVitals(patientId, null));
    }

    @Transactional(readOnly = true)
    public List<MedicationReferenceDto> getMedicationReferences() {
        return medicationReferenceRepository.findByStatusIgnoreCaseOrderByMedicationNameAsc("ACTIVE").stream()
                .map(reference -> new MedicationReferenceDto(
                        reference.getMedicationReferenceId().toString(),
                        reference.getMedicationCode(),
                        reference.getMedicationName(),
                        reference.getDosageForm(),
                        reference.getStrength()
                ))
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<VisitMedicationDto> getVisitMedications(String patientId, String visitId) {
        UUID patientUuid = UUID.fromString(patientId);
        patientRepository.findById(patientUuid)
                .orElseThrow(() -> new EntityNotFoundException("Patient not found"));

        if (visitId != null && !visitId.isBlank()) {
            Visit visit = resolveVisitForPatient(visitId, patientUuid);
            return visitMedicationRepository.findByVisit_VisitIdOrderByPrescribedAtDesc(visit.getVisitId()).stream()
                    .map(this::toVisitMedication)
                    .collect(Collectors.toList());
        }

        return visitMedicationRepository.findByPatient_PatientIdOrderByPrescribedAtDesc(patientUuid).stream()
                .map(this::toVisitMedication)
                .collect(Collectors.toList());
    }

    private PatientVitalsDto emptyVitals(String patientId, String visitId) {
        return new PatientVitalsDto(
                patientId,
                visitId,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null
        );
    }

    @Transactional
    public PatientVitalsDto savePatientVitals(SavePatientVitalsRequest request, String authorizationHeader) {
        UUID patientId = UUID.fromString(request.getPatientid());
        Patient patient = patientRepository.findById(patientId)
                .orElseThrow(() -> new EntityNotFoundException("Patient not found"));
        AppUser currentUser = authApiService.resolveCurrentUser(authorizationHeader)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Authentication required"));
        Visit visit = resolveVisitForPatient(request.getVisitid(), patientId);
        Nurse recordedByNurse = nurseRepository.findFirstByUser_UserIdAndStatus(currentUser.getUserId(), "ACTIVE")
                .orElse(null);

        VisitVital visitVital = new VisitVital();
        visitVital.setVisitVitalId(UUID.randomUUID());
        visitVital.setPatient(patient);
        visitVital.setVisit(visit);
        visitVital.setRecordedByNurse(recordedByNurse);
        visitVital.setSystolicBp(request.getSystolic_bp());
        visitVital.setDiastolicBp(request.getDiastolic_bp());
        visitVital.setPulseBpm(request.getPulse_bpm());
        visitVital.setTemperatureC(request.getTemperature_c());
        visitVital.setWeightKg(request.getWeight_kg());
        visitVital.setHeightCm(request.getHeight_cm());
        visitVital.setRecordedAt(LocalDateTime.now());

        return toPatientVitals(visitVitalRepository.save(visitVital));
    }

    @Transactional
    public VisitMedicationDto saveVisitMedication(SaveVisitMedicationRequest request, String authorizationHeader) {
        UUID patientId = UUID.fromString(request.getPatientid());
        Patient patient = patientRepository.findById(patientId)
                .orElseThrow(() -> new EntityNotFoundException("Patient not found"));
        Visit visit = resolveVisitForPatient(request.getVisitid(), patientId);
        MedicationReference medicationReference = resolveMedicationReference(request.getMedication_name());
        Doctor prescribingDoctor = resolvePrescribingDoctor(visit, authorizationHeader);

        VisitMedication visitMedication = new VisitMedication();
        visitMedication.setVisitMedicationId(UUID.randomUUID());
        visitMedication.setVisit(visit);
        visitMedication.setPatient(patient);
        visitMedication.setMedicationReference(medicationReference);
        visitMedication.setPrescribedByDoctor(prescribingDoctor);
        visitMedication.setDosageInstruction(request.getDosage_instruction().trim());
        visitMedication.setFrequency(request.getFrequency().trim());
        visitMedication.setDuration(request.getDuration().trim());
        visitMedication.setMedicationStatus(normalizeMedicationStatus(request.getMedication_status()));
        visitMedication.setPrescribedAt(LocalDateTime.now());

        return toVisitMedication(visitMedicationRepository.save(visitMedication));
    }

    @Transactional(readOnly = true)
    public String getDoctorId(String authorizationHeader) {
        Optional<Doctor> currentDoctor = authApiService.resolveCurrentUser(authorizationHeader)
                .flatMap(user -> doctorRepository.findFirstByUser_UserIdAndStatus(user.getUserId(), "ACTIVE"));
        if (currentDoctor.isPresent()) {
            return currentDoctor.get().getEmployeeNumber();
        }

        return doctorRepository.findFirstByStatusOrderByDoctorIdAsc("ACTIVE")
                .map(Doctor::getEmployeeNumber)
                .orElse("");
    }

    @Transactional(readOnly = true)
    public String getNurseId(String authorizationHeader) {
        Optional<Nurse> currentNurse = authApiService.resolveCurrentUser(authorizationHeader)
                .flatMap(user -> nurseRepository.findFirstByUser_UserIdAndStatus(user.getUserId(), "ACTIVE"));
        if (currentNurse.isPresent()) {
            return currentNurse.get().getNurseId().toString();
        }

        return nurseRepository.findFirstByStatusOrderByNurseIdAsc("ACTIVE")
                .map(nurse -> nurse.getNurseId().toString())
                .orElse("");
    }

    @Transactional
    public OrderResponse createOrder(OrderRequest request, String authorizationHeader) {
        Patient patient = patientRepository.findById(UUID.fromString(request.getPatientid()))
                .orElseThrow(() -> new EntityNotFoundException("Patient not found"));
        Doctor doctor = resolveDoctor(request.getEmployeeid(), authorizationHeader);
        Visit visit = visitRepository.findTopByPatient_PatientIdOrderByVisitDateDesc(patient.getPatientId())
                .orElseThrow(() -> new EntityNotFoundException("No visit found for patient"));

        OrderItem orderItem = new OrderItem();
        orderItem.setOrderId(resolveOrderId(request.getOrder_id()));
        orderItem.setVisit(visit);
        orderItem.setPatient(patient);
        orderItem.setOrderingDoctor(doctor);
        orderItem.setOrderType(request.getOrder_type());
        orderItem.setOrderName(request.getOrder_name());
        orderItem.setOrderStatus("ORDERED");
        orderItem.setNotes(null);
        orderItem.setOrderedAt(LocalDateTime.now());
        orderItem.setUpdatedAt(LocalDateTime.now());
        orderItemRepository.save(orderItem);

        return new OrderResponse(orderItem.getOrderId().toString(), "ORDERED", "Order created");
    }

    private MedicalRecordItemDto toMedicalRecordItem(MedicalRecordSection section) {
        AppUser recordedBy = section.getRecordedByUser();
        String recordedByName = recordedBy.getFirstName() + " " + recordedBy.getLastName();
        return new MedicalRecordItemDto(
                section.getSectionType(),
                section.getSectionTitle(),
                section.getSectionData(),
                section.getRecordedAt().format(DATE_TIME_FORMAT),
                recordedByName
        );
    }

    private PatientVitalsDto toPatientVitals(VisitVital visitVital) {
        Nurse recordedByNurse = visitVital.getRecordedByNurse();
        String recordedByName = recordedByNurse == null
                ? null
                : recordedByNurse.getUser().getFirstName() + " " + recordedByNurse.getUser().getLastName();

        return new PatientVitalsDto(
                visitVital.getPatient().getPatientId().toString(),
                visitVital.getVisit() == null ? null : visitVital.getVisit().getVisitId().toString(),
                visitVital.getSystolicBp(),
                visitVital.getDiastolicBp(),
                visitVital.getPulseBpm(),
                visitVital.getTemperatureC(),
                visitVital.getWeightKg(),
                visitVital.getHeightCm(),
                visitVital.getRecordedAt().format(DATE_TIME_FORMAT),
                recordedByName
        );
    }

    private VisitMedicationDto toVisitMedication(VisitMedication visitMedication) {
        Doctor doctor = visitMedication.getPrescribedByDoctor();
        String prescribedByName = doctor == null
                ? null
                : doctor.getUser().getFirstName() + " " + doctor.getUser().getLastName();

        return new VisitMedicationDto(
                visitMedication.getVisitMedicationId().toString(),
                visitMedication.getPatient().getPatientId().toString(),
                visitMedication.getVisit().getVisitId().toString(),
                visitMedication.getMedicationReference().getMedicationReferenceId().toString(),
                visitMedication.getMedicationReference().getMedicationName(),
                visitMedication.getDosageInstruction(),
                visitMedication.getFrequency(),
                visitMedication.getDuration(),
                visitMedication.getMedicationStatus(),
                visitMedication.getPrescribedAt().format(DATE_TIME_FORMAT),
                prescribedByName
        );
    }

    private UUID resolveOrderId(String requestedOrderId) {
        if (requestedOrderId == null || requestedOrderId.isBlank()) {
            return UUID.randomUUID();
        }
        return UUID.fromString(requestedOrderId);
    }

    private Doctor resolveDoctor(String employeeIdOrDoctorId, String authorizationHeader) {
        if (employeeIdOrDoctorId == null || employeeIdOrDoctorId.isBlank()) {
            Optional<Doctor> authenticatedDoctor = authApiService.resolveCurrentUser(authorizationHeader)
                    .flatMap(user -> doctorRepository.findFirstByUser_UserIdAndStatus(user.getUserId(), "ACTIVE"));
            if (authenticatedDoctor.isPresent()) {
                return authenticatedDoctor.get();
            }
        }

        if (employeeIdOrDoctorId == null || employeeIdOrDoctorId.isBlank()) {
            throw new EntityNotFoundException("Doctor identifier is required");
        }

        try {
            UUID doctorId = UUID.fromString(employeeIdOrDoctorId);
            return doctorRepository.findById(doctorId)
                    .orElseThrow(() -> new EntityNotFoundException("Doctor not found"));
        } catch (IllegalArgumentException ex) {
            return doctorRepository.findByEmployeeNumber(employeeIdOrDoctorId)
                    .orElseThrow(() -> new EntityNotFoundException("Doctor not found"));
        }
    }

    private Visit resolveVisitForPatient(String visitId, UUID patientId) {
        Visit visit = visitRepository.findById(UUID.fromString(visitId))
                .orElseThrow(() -> new EntityNotFoundException("Visit not found"));

        if (!visit.getPatient().getPatientId().equals(patientId)) {
            throw new IllegalArgumentException("Visit does not belong to the provided patient");
        }

        return visit;
    }

    private MedicationReference resolveMedicationReference(String medicationName) {
        String cleanName = medicationName == null ? "" : medicationName.trim();
        if (cleanName.isEmpty()) {
            throw new IllegalArgumentException("Medication name is required");
        }

        return medicationReferenceRepository.findFirstByMedicationNameIgnoreCase(cleanName)
                .orElseGet(() -> {
                    MedicationReference reference = new MedicationReference();
                    reference.setMedicationReferenceId(UUID.randomUUID());
                    reference.setMedicationCode(generateMedicationCode());
                    reference.setMedicationName(cleanName);
                    reference.setStatus("ACTIVE");
                    reference.setCreatedAt(LocalDateTime.now());
                    reference.setUpdatedAt(LocalDateTime.now());
                    return medicationReferenceRepository.save(reference);
                });
    }

    private Doctor resolvePrescribingDoctor(Visit visit, String authorizationHeader) {
        if (visit.getAssignedDoctor() != null) {
            return visit.getAssignedDoctor();
        }

        Optional<Doctor> authenticatedDoctor = authApiService.resolveCurrentUser(authorizationHeader)
                .flatMap(user -> doctorRepository.findFirstByUser_UserIdAndStatus(user.getUserId(), "ACTIVE"));
        if (authenticatedDoctor.isPresent()) {
            return authenticatedDoctor.get();
        }

        return doctorRepository.findFirstByStatusOrderByDoctorIdAsc("ACTIVE")
                .orElseThrow(() -> new EntityNotFoundException("No active doctor found for medication entry"));
    }

    private String normalizeMedicationStatus(String status) {
        if (status == null || status.isBlank()) {
            return "ACTIVE";
        }
        return status.trim().toUpperCase(Locale.ROOT);
    }

    private String generateMedicationCode() {
        return "MED-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase(Locale.ROOT);
    }
}
