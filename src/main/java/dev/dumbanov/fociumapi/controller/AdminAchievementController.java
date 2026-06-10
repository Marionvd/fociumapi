package dev.dumbanov.fociumapi.controller;

import dev.dumbanov.fociumapi.dto.achievement.AchievementCreateInput;
import dev.dumbanov.fociumapi.dto.achievement.AchievementOutput;
import dev.dumbanov.fociumapi.dto.achievement.AchievementUpdateInput;
import dev.dumbanov.fociumapi.entity.Achievement;
import dev.dumbanov.fociumapi.exception.BadRequestException;
import dev.dumbanov.fociumapi.service.AchievementService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/admin/achievements")
@RequiredArgsConstructor
public class AdminAchievementController {
    private final AchievementService achievementService;

    @PostMapping
    public ResponseEntity<?> createAchievement(@Valid @RequestBody AchievementCreateInput input) {
        try {
            Achievement a = achievementService.createAchievement(input);
            return ResponseEntity.created(URI.create("/api/admin/achievements/" + a.getUuid())).body(AchievementOutput.of(a));
        } catch (BadRequestException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        } catch (RuntimeException e) {
            return ResponseEntity.internalServerError().body(Map.of("error", e.getMessage()));
        }
    }

    @PutMapping("/{uuid}")
    public ResponseEntity<?> updateAchievement(@PathVariable UUID uuid, @RequestBody AchievementUpdateInput input) {
        try {
            Achievement a = achievementService.updateAchievement(uuid, input);
            return ResponseEntity.ok(AchievementOutput.of(a));
        } catch (BadRequestException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        } catch (RuntimeException e) {
            return ResponseEntity.internalServerError().body(Map.of("error", e.getMessage()));
        }
    }

    @DeleteMapping("/{uuid}")
    public ResponseEntity<?> deleteAchievement(@PathVariable UUID uuid) {
        try {
            achievementService.deleteAchievement(uuid);
            return ResponseEntity.ok(Map.of("message", "Achievement deleted."));
        } catch (BadRequestException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        } catch (RuntimeException e) {
            return ResponseEntity.internalServerError().body(Map.of("error", e.getMessage()));
        }
    }
}

