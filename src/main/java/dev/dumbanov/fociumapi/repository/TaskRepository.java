package dev.dumbanov.fociumapi.repository;

import dev.dumbanov.fociumapi.entity.Profile;
import dev.dumbanov.fociumapi.entity.Task;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface TaskRepository extends JpaRepository<Task, UUID> {
	Optional<Task> findTaskByUuidAndProfile_Uuid(UUID taskUuid, UUID profileUuid);

	List<Task> findAllByProfile(Profile profile);
}
