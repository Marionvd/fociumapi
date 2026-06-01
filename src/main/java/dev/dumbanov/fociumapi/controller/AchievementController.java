package dev.dumbanov.fociumapi.controller;

import dev.dumbanov.fociumapi.dto.achievement.AchievementOutput;
import dev.dumbanov.fociumapi.repository.AchievementRepository;
import dev.dumbanov.fociumapi.service.ProfileService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/achievements")
@RequiredArgsConstructor
public class AchievementController {
    private final AchievementRepository achievementRepository;
    private final ProfileService profileService;

    /** All achievements in the system (for the catalogue view). */
    @GetMapping
    public ResponseEntity<?> listAchievements() {
        List<AchievementOutput> list = achievementRepository.findAll()
                .stream().map(AchievementOutput::of).toList();
        return ResponseEntity.ok(list);
    }

    /** Achievements already earned by the currently logged-in user. */
    @GetMapping("/me")
    public ResponseEntity<?> getMyAchievements(@AuthenticationPrincipal String email) {
        try {
            var profile = profileService.getProfileByEmail(email);
            List<AchievementOutput> list = profile.getAchievements()
                    .stream().map(AchievementOutput::of).toList();
            return ResponseEntity.ok(list);
        } catch (RuntimeException e) {
            return ResponseEntity.internalServerError().body(Map.of("error", e.getMessage()));
        }
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
