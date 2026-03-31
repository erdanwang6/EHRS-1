package com.ehrs.backend.repository;

import com.ehrs.backend.model.DiagnosisReference;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DiagnosisReferenceRepository extends JpaRepository<DiagnosisReference, UUID> {

    List<DiagnosisReference> findByStatusIgnoreCaseOrderByDiagnosisCodeAsc(String status);
}
