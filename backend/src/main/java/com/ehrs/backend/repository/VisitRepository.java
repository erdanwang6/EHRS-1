package com.ehrs.backend.repository;

import com.ehrs.backend.model.Visit;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface VisitRepository extends JpaRepository<Visit, UUID> {

    List<Visit> findAllByOrderByVisitDateDesc();

    List<Visit> findByCurrentStatusIgnoreCaseOrderByVisitDateDesc(String currentStatus);

    List<Visit> findByPatient_PatientIdOrderByVisitDateDesc(UUID patientId);

    Optional<Visit> findTopByPatient_PatientIdOrderByVisitDateDesc(UUID patientId);

    Optional<Visit> findTopByAssignedDoctor_DoctorIdAndCurrentStatusIgnoreCaseOrderByVisitDateDesc(UUID doctorId, String currentStatus);

    List<Visit> findByAssignedDoctor_DoctorIdAndCurrentStatusIgnoreCaseOrderByVisitDateDesc(UUID doctorId, String currentStatus);
}
