package com.ehrs.backend.repository;

import com.ehrs.backend.model.AppUser;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface AppUserRepository extends JpaRepository<AppUser, UUID> {

    Optional<AppUser> findFirstByStatusOrderByCreatedAtAsc(String status);

    Optional<AppUser> findByUsernameIgnoreCase(String username);

    Optional<AppUser> findByUserIdAndStatus(UUID userId, String status);

    @Query(value = "SELECT r.role_code FROM dbo.USER_ROLE ur INNER JOIN dbo.ROLE r ON ur.role_id = r.role_id WHERE ur.user_id = :userId ORDER BY r.role_code",
            nativeQuery = true)
    List<String> findRoleCodesByUserId(@Param("userId") UUID userId);
}
