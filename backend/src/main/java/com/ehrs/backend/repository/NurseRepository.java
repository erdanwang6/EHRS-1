package com.ehrs.backend.repository;

import com.ehrs.backend.model.Nurse;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NurseRepository extends JpaRepository<Nurse, UUID> {

    Optional<Nurse> findFirstByStatusOrderByNurseIdAsc(String status);

    Optional<Nurse> findFirstByUser_UserIdAndStatus(UUID userId, String status);
}
