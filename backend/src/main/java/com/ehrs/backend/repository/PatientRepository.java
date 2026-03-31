package com.ehrs.backend.repository;

import com.ehrs.backend.model.Patient;
import java.util.UUID;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PatientRepository extends JpaRepository<Patient, UUID> {
    Optional<Patient> findByPatientId(UUID patientId);
}
