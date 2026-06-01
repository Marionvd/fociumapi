package dev.dumbanov.fociumapi.controller;

import com.cloudinary.Cloudinary;
import dev.dumbanov.fociumapi.dto.profile.ProfileOutput;
import dev.dumbanov.fociumapi.entity.Profile;
import dev.dumbanov.fociumapi.repository.ProfileRepository;
import dev.dumbanov.fociumapi.service.ProfileService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;

@RestController
@RequestMapping("/api/profiles")
@RequiredArgsConstructor
public class ProfileController {
    private final ProfileRepository profileRepository;
    private final ProfileService profileService;

    @GetMapping("/me")
    public ResponseEntity<?> getMyProfile(@AuthenticationPrincipal String email) {
        var profile = profileService.getProfileByEmail(email);
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

    @PostMapping("/pfp")
    public ResponseEntity<?> uploadPfp(@AuthenticationPrincipal String email, @RequestParam("file") MultipartFile file) {
        try {
            var profile = profileService.uploadPfp(file, email);

            return ResponseEntity.ok(Map.of("pfpUrl", profile.getPfp()));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        } catch (IOException e) {
            return ResponseEntity.status(500).body(Map.of("error", "Failed to upload profile picture."));
        }
    }
}
