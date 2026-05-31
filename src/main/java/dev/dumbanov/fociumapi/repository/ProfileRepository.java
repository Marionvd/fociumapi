package dev.dumbanov.fociumapi.repository;

import dev.dumbanov.fociumapi.entity.Profile;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface ProfileRepository extends JpaRepository<Profile, UUID> {
    Profile findProfileByEmail(String email);

    boolean existsProfileByEmail(String email);

    List<Profile> findAllByOrderByTotalPointsDesc(Pageable pageable);
}
