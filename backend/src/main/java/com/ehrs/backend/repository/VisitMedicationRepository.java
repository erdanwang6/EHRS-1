package com.ehrs.backend.repository;

import com.ehrs.backend.model.VisitMedication;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface VisitMedicationRepository extends JpaRepository<VisitMedication, UUID> {

    List<VisitMedication> findByVisit_VisitIdOrderByPrescribedAtDesc(UUID visitId);

    List<VisitMedication> findByPatient_PatientIdOrderByPrescribedAtDesc(UUID patientId);
}
