package dev.dumbanov.fociumapi.repository;

import dev.dumbanov.fociumapi.entity.Profile;
import dev.dumbanov.fociumapi.entity.Showdown;
import dev.dumbanov.fociumapi.entity.types.ShowdownStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface ShowdownRepository extends JpaRepository<Showdown, UUID> {
    List<Showdown> findByStatus(ShowdownStatus status);

    @Query("""
            SELECT s FROM Showdown s
            WHERE (s.profile1 = :profile OR s.profile2 = :profile)
            AND s.status = :status
            """)
    List<Showdown> findShowdownsByProfileAndStatus(@Param("profile") Profile profile, @Param("status") ShowdownStatus status);

    @Query("""
            SELECT s FROM Showdown s
            WHERE (s.profile1 = :profile OR s.profile2 = :profile)
            ORDER BY s.createdAt DESC
            """)
    List<Showdown> findShowdownsByProfile(@Param("profile") Profile profile);

    @Query("""
            SELECT s FROM Showdown s
            WHERE s.status = :status
            AND s.endTime <= :now
            """)
    List<Showdown> findExpiredShowdowns(@Param("status") ShowdownStatus status, @Param("now") LocalDateTime now);
}

