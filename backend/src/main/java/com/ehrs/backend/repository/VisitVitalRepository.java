package com.ehrs.backend.repository;

import com.ehrs.backend.model.VisitVital;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface VisitVitalRepository extends JpaRepository<VisitVital, UUID> {

    Optional<VisitVital> findFirstByVisit_VisitIdOrderByRecordedAtDesc(UUID visitId);

    Optional<VisitVital> findFirstByPatient_PatientIdOrderByRecordedAtDesc(UUID patientId);
}
