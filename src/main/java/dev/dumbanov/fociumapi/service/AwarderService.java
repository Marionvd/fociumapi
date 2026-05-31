package dev.dumbanov.fociumapi.service;

import dev.dumbanov.fociumapi.dto.task.TaskCompletionEvaluation;
import dev.dumbanov.fociumapi.entity.Profile;
import dev.dumbanov.fociumapi.entity.Task;
import dev.dumbanov.fociumapi.entity.types.PrerequisiteType;
import dev.dumbanov.fociumapi.repository.AchievementPrerequisiteRepository;
import dev.dumbanov.fociumapi.repository.AchievementRepository;
import dev.dumbanov.fociumapi.repository.ProfileRepository;
import dev.dumbanov.fociumapi.repository.TaskRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AwarderService {
    private final ProfileRepository profileRepository;
    private final AchievementRepository achievementRepository;
    private final AchievementPrerequisiteRepository achievementPrerequisiteRepository;
    private final TaskRepository taskRepository;

    void checkForAchievementCompletion(PrerequisiteType prerequisiteType, Profile profile) {
        var prerequisites = achievementPrerequisiteRepository.findAllByType(prerequisiteType);

        for(var prerequisite : prerequisites) {
            if(profile.getAchievements().contains(prerequisite.getAchievement())) {
                continue;
            }

            switch (prerequisiteType) {
                case PrerequisiteType.POINTS_AMOUNT -> {
                    if(prerequisite.getRequiredAmount() > profile.getTotalPoints()) {
                        continue;
                    }
                }
                case PrerequisiteType.DAY_STREAK -> {
                    if(prerequisite.getRequiredAmount() > profile.getDayStreak()) {
                        continue;
                    }
                }
            };

            profile.setTotalPoints(profile.getTotalPoints() + prerequisite.getAchievement().getRewardPoints());
            profile.setPoints(profile.getPoints() + prerequisite.getAchievement().getRewardPoints());
            profile.getAchievements().add(prerequisite.achievement);
            profileRepository.save(profile);
        }
    }

    public void awardTaskCompletion(TaskCompletionEvaluation evaluation, Task task, Profile profile) {
        if(task.getAwardedPoints()!=null && evaluation.points() <= task.getAwardedPoints()) {
            return;
        }

        int pointsToAward = evaluation.points();

        profile.setPoints(profile.getPoints() + pointsToAward - (task.getAwardedPoints()==null?0:task.getAwardedPoints()));
        profile.setTotalPoints(profile.getTotalPoints() + pointsToAward - (task.getAwardedPoints()==null?0:task.getAwardedPoints()));
        task.setAwardedPoints(pointsToAward);
        task.setAiFeedback(evaluation.feedback());
        taskRepository.save(task);

        profileRepository.save(profile);

        checkForAchievementCompletion(PrerequisiteType.POINTS_AMOUNT, profile);
    }

    public void manageDayStreak(Profile profile) {
        if(profile.getLastLogin() == null) {
            profile.setDayStreak(1);
        } else {
            var daysBetween = java.time.Duration.between(profile.getLastLogin(), java.time.LocalDateTime.now()).toDays();
            if(daysBetween == 1) {
                profile.setDayStreak(profile.getDayStreak() + 1);
            } else if(daysBetween > 1) {
                profile.setDayStreak(1);
            }
        }
    }
}
