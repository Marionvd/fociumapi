package dev.dumbanov.fociumapi.controller;

import dev.dumbanov.fociumapi.dto.achievement.AchievementOutput;
import dev.dumbanov.fociumapi.dto.task.TaskCompletionEvaluation;
import dev.dumbanov.fociumapi.entity.Achievement;
import dev.dumbanov.fociumapi.exception.BadRequestException;
import dev.dumbanov.fociumapi.repository.AchievementRepository;
import dev.dumbanov.fociumapi.service.AIService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/achievements")
@RequiredArgsConstructor
public class AchievementController {
    private final AchievementRepository achievementRepository;

    @GetMapping
    public ResponseEntity<?> listAchievements() {
        List<AchievementOutput> list = achievementRepository.findAll().stream().map(AchievementOutput::of).toList();
        return ResponseEntity.ok(list);
    }

    @GetMapping("/{uuid}")
    public ResponseEntity<?> getAchievement(@PathVariable UUID uuid) {
        var opt = achievementRepository.findById(uuid);
        if (opt.isEmpty()) {
            return ResponseEntity.status(404).body(Map.of("error", "Achievement not found"));
        }
        return ResponseEntity.ok(AchievementOutput.of(opt.get()));
    }

}




