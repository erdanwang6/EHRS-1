package com.ehrs.backend.repository;

import com.ehrs.backend.model.MedicationReference;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MedicationReferenceRepository extends JpaRepository<MedicationReference, UUID> {

    List<MedicationReference> findByStatusIgnoreCaseOrderByMedicationNameAsc(String status);

    Optional<MedicationReference> findFirstByMedicationNameIgnoreCase(String medicationName);
}
