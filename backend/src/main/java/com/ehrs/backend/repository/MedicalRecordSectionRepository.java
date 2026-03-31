package com.ehrs.backend.repository;

import com.ehrs.backend.model.MedicalRecordSection;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MedicalRecordSectionRepository extends JpaRepository<MedicalRecordSection, UUID> {

    List<MedicalRecordSection> findByPatient_PatientIdOrderByRecordedAtDesc(UUID patientId);

    Optional<MedicalRecordSection> findFirstByPatient_PatientIdAndSectionTypeIgnoreCaseOrderByRecordedAtDesc(
            UUID patientId,
            String sectionType
    );
}
