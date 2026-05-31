package dev.dumbanov.fociumapi.service;

import dev.dumbanov.fociumapi.dto.task.TaskCompletionEvaluation;
import dev.dumbanov.fociumapi.entity.Profile;
import dev.dumbanov.fociumapi.entity.Task;
import dev.dumbanov.fociumapi.repository.ProfileRepository;
import dev.dumbanov.fociumapi.repository.TaskRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AwarderService {
    private final ProfileRepository profileRepository;
    private final TaskRepository taskRepository;

    public void awardTaskCompletion(TaskCompletionEvaluation evaluation, Task task, Profile profile) {
        if(task.getAwardedPoints()!=null && evaluation.points() <= task.getAwardedPoints()) {
            return;
        }

        int pointsToAward = evaluation.points();
        
        profile.setPoints(profile.getPoints() + pointsToAward);
        task.setAwardedPoints(pointsToAward);
        task.setAiFeedback(evaluation.feedback());
        taskRepository.save(task);

        profileRepository.save(profile);
    }

}
