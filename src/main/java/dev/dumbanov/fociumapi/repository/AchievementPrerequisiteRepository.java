package dev.dumbanov.fociumapi.repository;

import dev.dumbanov.fociumapi.entity.AchievementPrerequisite;
import dev.dumbanov.fociumapi.entity.types.PrerequisiteType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AchievementPrerequisiteRepository extends JpaRepository<dev.dumbanov.fociumapi.entity.AchievementPrerequisite, java.util.UUID> {
	void deleteByAchievement(dev.dumbanov.fociumapi.entity.Achievement achievement);

	List<AchievementPrerequisite> findAllByType(PrerequisiteType type);
}
