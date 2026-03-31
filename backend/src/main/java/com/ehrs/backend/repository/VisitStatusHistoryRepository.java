package com.ehrs.backend.repository;

import com.ehrs.backend.model.VisitStatusHistory;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface VisitStatusHistoryRepository extends JpaRepository<VisitStatusHistory, UUID> {
}
