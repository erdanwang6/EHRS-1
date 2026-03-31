package com.ehrs.backend.repository;

import com.ehrs.backend.model.Doctor;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DoctorRepository extends JpaRepository<Doctor, UUID> {

    Optional<Doctor> findFirstByStatusOrderByDoctorIdAsc(String status);

    Optional<Doctor> findByEmployeeNumber(String employeeNumber);

    Optional<Doctor> findFirstByUser_UserIdAndStatus(UUID userId, String status);
}
