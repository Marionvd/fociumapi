package dev.dumbanov.fociumapi.service;

import dev.dumbanov.fociumapi.dto.task.TaskCompletionEvaluation;
import dev.dumbanov.fociumapi.entity.Achievement;
import dev.dumbanov.fociumapi.entity.Profile;
import dev.dumbanov.fociumapi.entity.Task;
import dev.dumbanov.fociumapi.entity.types.PrerequisiteType;
import dev.dumbanov.fociumapi.repository.AchievementPrerequisiteRepository;
import dev.dumbanov.fociumapi.repository.AchievementRepository;
import dev.dumbanov.fociumapi.repository.ProfileRepository;
import dev.dumbanov.fociumapi.repository.TaskRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AwarderService {
    private final ProfileRepository profileRepository;
    private final AchievementRepository achievementRepository;
    private final AchievementPrerequisiteRepository achievementPrerequisiteRepository;
    private final TaskRepository taskRepository;

    /**
     * Checks prerequisites of the given type and unlocks any newly qualifying achievements.
     * @return List of achievements that were just unlocked (empty if none).
     */
    List<Achievement> checkForAchievementCompletion(PrerequisiteType prerequisiteType, Profile profile) {
        List<Achievement> newlyEarned = new ArrayList<>();
        var prerequisites = achievementPrerequisiteRepository.findAllByType(prerequisiteType);

        for (var prerequisite : prerequisites) {
            if (profile.getAchievements().contains(prerequisite.getAchievement())) {
                continue;
            }

            boolean qualifies = switch (prerequisiteType) {
                case POINTS_AMOUNT -> prerequisite.getRequiredAmount() <= profile.getTotalPoints();
                case DAY_STREAK    -> prerequisite.getRequiredAmount() <= profile.getDayStreak();
            };

            if (!qualifies) continue;

            profile.setTotalPoints(profile.getTotalPoints() + prerequisite.getAchievement().getRewardPoints());
            profile.setPoints(profile.getPoints() + prerequisite.getAchievement().getRewardPoints());
            profile.getAchievements().add(prerequisite.achievement);
            profileRepository.save(profile);
            newlyEarned.add(prerequisite.getAchievement());
        }

        return newlyEarned;
    }

    /**
     * Awards points for completing a task and checks for newly unlocked achievements.
     * @return List of achievements unlocked by this action.
     */
    public List<Achievement> awardTaskCompletion(TaskCompletionEvaluation evaluation, Task task, Profile profile) {
        if (task.getAwardedPoints() != null && evaluation.points() <= task.getAwardedPoints()) {
            return List.of();
        }

        int pointsToAward = evaluation.points();
        int previouslyAwarded = task.getAwardedPoints() == null ? 0 : task.getAwardedPoints();

        profile.setPoints(profile.getPoints() + pointsToAward - previouslyAwarded);
        profile.setTotalPoints(profile.getTotalPoints() + pointsToAward - previouslyAwarded);
        task.setAwardedPoints(pointsToAward);
        task.setAiFeedback(evaluation.feedback());
        taskRepository.save(task);
        profileRepository.save(profile);

        return checkForAchievementCompletion(PrerequisiteType.POINTS_AMOUNT, profile);
    }

    /**
     * Awards the showdown winner 100 bonus points and checks for achievements.
     * @return List of achievements unlocked by this action.
     */
    public List<Achievement> awardShowdownWin(Profile winner) {
        winner.setPoints(winner.getPoints() + 100);
        winner.setTotalPoints(winner.getTotalPoints() + 100);
        profileRepository.save(winner);
        return checkForAchievementCompletion(PrerequisiteType.POINTS_AMOUNT, winner);
    }

    public void manageDayStreak(Profile profile) {
        if (profile.getLastLogin() == null) {
            profile.setDayStreak(1);
        } else {
            long daysBetween = java.time.Duration.between(profile.getLastLogin(), java.time.LocalDateTime.now()).toDays();
            if (daysBetween == 1) {
                profile.setDayStreak(profile.getDayStreak() + 1);
                checkForAchievementCompletion(PrerequisiteType.DAY_STREAK, profile);
            } else if (daysBetween > 1) {
                profile.setDayStreak(1);
            }
        }
        profileRepository.save(profile);
    }
}
