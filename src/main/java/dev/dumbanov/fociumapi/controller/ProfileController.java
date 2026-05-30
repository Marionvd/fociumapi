package dev.dumbanov.fociumapi.controller;

import dev.dumbanov.fociumapi.dto.profile.ProfileOutput;
import dev.dumbanov.fociumapi.entity.Profile;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/profiles")
public class ProfileController {
    @GetMapping("/me")
    public ResponseEntity<?> getMyProfile(@AuthenticationPrincipal Profile profile) {
        return ResponseEntity.ok(ProfileOutput.of(profile));

    }
}
