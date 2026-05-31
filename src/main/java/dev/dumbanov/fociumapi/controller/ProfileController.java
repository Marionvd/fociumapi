package dev.dumbanov.fociumapi.controller;

import dev.dumbanov.fociumapi.dto.profile.ProfileOutput;
import dev.dumbanov.fociumapi.entity.Profile;
import dev.dumbanov.fociumapi.repository.ProfileRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/profiles")
@RequiredArgsConstructor
public class ProfileController {

    private final ProfileRepository profileRepository;

    @GetMapping("/me")
    public ResponseEntity<?> getMyProfile(@AuthenticationPrincipal Profile profile) {
        return ResponseEntity.ok(ProfileOutput.of(profile));
    }

    @GetMapping("/search")
    public ResponseEntity<?> searchProfiles(@RequestParam String email) {
        if (email == null || email.isBlank() || email.length() < 2) {
            return ResponseEntity.badRequest().body(Map.of("error", "Search query must be at least 2 characters."));
        }
        var results = profileRepository
                .findTop10ByEmailContainingIgnoreCaseOrderByTotalPointsDesc(email)
                .stream()
                .map(ProfileOutput::of)
                .toList();
        return ResponseEntity.ok(results);
    }
}
