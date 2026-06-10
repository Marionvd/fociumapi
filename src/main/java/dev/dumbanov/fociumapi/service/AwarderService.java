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
    private final PerkService perkService;

    // ── Achievements ──────────────────────────────────────────────────────────

    List<Achievement> checkForAchievementCompletion(PrerequisiteType prerequisiteType, Profile profile) {
        List<Achievement> newlyEarned = new ArrayList<>();
        var prerequisites = achievementPrerequisiteRepository.findAllByType(prerequisiteType);

        for (var prerequisite : prerequisites) {
            if (profile.getAchievements().contains(prerequisite.getAchievement())) continue;

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

    // ── Task completion ───────────────────────────────────────────────────────

    /**
     * Result returned from {@link #awardTaskCompletion}.
     * Carries both the newly-unlocked achievements and the boost that was active.
     */
    public record TaskAwardResult(List<Achievement> newAchievements, int boostPercent) {}

    /**
     * Awards points for completing a task, applying any active POINTS_BOOST perks.
     * Returns the list of newly-unlocked achievements and the applied boost percentage.
     */
    public TaskAwardResult awardTaskCompletion(TaskCompletionEvaluation evaluation, Task task, Profile profile) {
        if (task.getAwardedPoints() != null && evaluation.points() <= task.getAwardedPoints()) {
            return new TaskAwardResult(List.of(), 0);
        }

        // Apply any active points boost
        int boostPercent = perkService.getTotalPointsBoostPercent(profile);
        int basePoints   = evaluation.points();
        int pointsToAward = boostPercent > 0
                ? basePoints + (basePoints * boostPercent / 100)
                : basePoints;

        int previouslyAwarded = task.getAwardedPoints() == null ? 0 : task.getAwardedPoints();
        profile.setPoints(profile.getPoints() + pointsToAward - previouslyAwarded);
        profile.setTotalPoints(profile.getTotalPoints() + pointsToAward - previouslyAwarded);

        task.setAwardedPoints(pointsToAward);
        task.setAiFeedback(evaluation.feedback());
        taskRepository.save(task);
        profileRepository.save(profile);

        List<Achievement> newAchievements = checkForAchievementCompletion(PrerequisiteType.POINTS_AMOUNT, profile);
        return new TaskAwardResult(newAchievements, boostPercent);
    }

    // ── Showdown win ──────────────────────────────────────────────────────────

    public List<Achievement> awardShowdownWin(Profile winner) {
        winner.setPoints(winner.getPoints() + 100);
        winner.setTotalPoints(winner.getTotalPoints() + 100);
        profileRepository.save(winner);
        return checkForAchievementCompletion(PrerequisiteType.POINTS_AMOUNT, winner);
    }

    // ── Day streak ────────────────────────────────────────────────────────────

    /**
     * Updates the day streak on login.
     * If the streak would break and the user has a STREAK_SHIELD, the shield is
     * consumed and the streak is preserved.
     */
    public void manageDayStreak(String email) {
        if(!profileRepository.existsProfileByEmail(email)) {
            return;
        }

        var profile = profileRepository.findProfileByEmail(email);
        if (profile.getLastLogin() == null) {
            profile.setDayStreak(1);
        } else {
            long daysBetween = java.time.Duration.between(
                    profile.getLastLogin(), java.time.LocalDateTime.now()).toDays();

            if (daysBetween == 1) {
                profile.setDayStreak(profile.getDayStreak() + 1);
                checkForAchievementCompletion(PrerequisiteType.DAY_STREAK, profile);

            } else if (daysBetween > 1) {
                // Try to absorb the miss with a streak shield before resetting
                boolean shielded = perkService.consumeStreakShield(profile);
                if (!shielded) {
                    profile.setDayStreak(1);
                }
                // If shielded: streak stays intact, the consumed shield is already saved
            }
        }
        profileRepository.save(profile);
    }
}
