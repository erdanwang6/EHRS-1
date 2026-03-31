package com.ehrs.backend.service;

import com.ehrs.backend.dto.CreatePatientRequest;
import com.ehrs.backend.dto.DoctorSummaryDto;
import com.ehrs.backend.dto.PatientDetailDto;
import com.ehrs.backend.dto.PatientSummaryDto;
import com.ehrs.backend.model.Doctor;
import com.ehrs.backend.model.Patient;
import javax.persistence.EntityNotFoundException;
import com.ehrs.backend.repository.DoctorRepository;
import com.ehrs.backend.repository.PatientRepository;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;
import java.util.UUID;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class PeopleApiService {

    private static final DateTimeFormatter DOB_FORMAT = DateTimeFormatter.ofPattern("MM/dd/yyyy");

    private final PatientRepository patientRepository;
    private final DoctorRepository doctorRepository;

    public PeopleApiService(PatientRepository patientRepository, DoctorRepository doctorRepository) {
        this.patientRepository = patientRepository;
        this.doctorRepository = doctorRepository;
    }

    @Transactional(readOnly = true)
    public List<PatientSummaryDto> getPatients() {
        return patientRepository.findAll().stream()
                .map(this::toPatientSummary)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<DoctorSummaryDto> getDoctors() {
        return doctorRepository.findAll().stream()
                .map(this::toDoctorSummary)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public PatientDetailDto getPatient(String patientId) {
        Patient patient = patientRepository.findByPatientId(UUID.fromString(patientId))
                .orElseThrow(() -> new EntityNotFoundException("Patient not found"));
        return toPatientDetail(patient);
    }

    @Transactional
    public PatientSummaryDto createPatient(CreatePatientRequest request) {
        Patient patient = new Patient();
        patient.setPatientId(UUID.randomUUID());
        patient.setMedicalRecordNumber(generateMedicalRecordNumber());
        patient.setFirstName(request.getFirstName().trim());
        patient.setMiddleName(clean(request.getMiddleName()));
        patient.setLastName(request.getLastName().trim());
        patient.setDateOfBirth(request.getDateOfBirth());
        patient.setSex(clean(request.getSex()));
        patient.setStatus("ACTIVE");
        patient.setCreatedAt(LocalDateTime.now());
        patient.setUpdatedAt(LocalDateTime.now());

        return toPatientSummary(patientRepository.save(patient));
    }

    private PatientSummaryDto toPatientSummary(Patient patient) {
        String nameBirth = patient.getFirstName()
                + " "
                + patient.getLastName()
                + " "
                + patient.getDateOfBirth().format(DOB_FORMAT);
        return new PatientSummaryDto(patient.getPatientId().toString(), nameBirth);
    }

    private DoctorSummaryDto toDoctorSummary(Doctor doctor) {
        String nameBirth = doctor.getUser().getFirstName() + " " + doctor.getUser().getLastName();
        return new DoctorSummaryDto(doctor.getEmployeeNumber(), nameBirth);
    }

    private PatientDetailDto toPatientDetail(Patient patient) {
        String fullName = patient.getMiddleName() == null || patient.getMiddleName().isBlank()
                ? patient.getFirstName() + " " + patient.getLastName()
                : patient.getFirstName() + " " + patient.getMiddleName() + " " + patient.getLastName();

        return new PatientDetailDto(
                patient.getPatientId().toString(),
                patient.getMedicalRecordNumber(),
                patient.getFirstName(),
                patient.getMiddleName(),
                patient.getLastName(),
                fullName,
                patient.getDateOfBirth().format(DOB_FORMAT),
                patient.getSex(),
                patient.getStatus()
        );
    }

    private String generateMedicalRecordNumber() {
        return "MRN-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase(Locale.ROOT);
    }

    private String clean(String value) {
        if (value == null) {
            return null;
        }

        String cleaned = value.trim();
        return cleaned.isEmpty() ? null : cleaned;
    }
}
