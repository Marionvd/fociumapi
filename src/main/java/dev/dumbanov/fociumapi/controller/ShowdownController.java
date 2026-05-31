package dev.dumbanov.fociumapi.controller;

import dev.dumbanov.fociumapi.dto.showdown.ShowdownOutput;
import dev.dumbanov.fociumapi.entity.Profile;
import dev.dumbanov.fociumapi.entity.Showdown;
import dev.dumbanov.fociumapi.exception.BadRequestException;
import dev.dumbanov.fociumapi.repository.ProfileRepository;
import dev.dumbanov.fociumapi.service.ShowdownService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/showdowns")
@RequiredArgsConstructor
public class ShowdownController {
    private final ShowdownService showdownService;
    private final ProfileRepository profileRepository;

    @GetMapping("/me")
    public ResponseEntity<?> getMyShowdowns(@AuthenticationPrincipal Profile profile) {
        try {
            List<Showdown> showdowns = showdownService.getShowdownsForProfile(profile);
            List<ShowdownOutput> output = showdowns.stream().map(ShowdownOutput::of).toList();
            return ResponseEntity.ok(output);
        } catch (RuntimeException e) {
            return ResponseEntity.internalServerError().body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/{uuid}")
    public ResponseEntity<?> getShowdown(@PathVariable UUID uuid) {
        try {
            Showdown s = showdownService.getShowdown(uuid);
            if (s == null) {
                return ResponseEntity.status(404).body(Map.of("error", "Showdown not found"));
            }
            return ResponseEntity.ok(ShowdownOutput.of(s));
        } catch (RuntimeException e) {
            return ResponseEntity.internalServerError().body(Map.of("error", e.getMessage()));
        }
    }

    @PostMapping("/{uuid}/complete")
    public ResponseEntity<?> completeShowdown(@PathVariable UUID uuid) {
        try {
            Showdown s = showdownService.completeShowdown(uuid);
            return ResponseEntity.ok(ShowdownOutput.of(s));
        } catch (BadRequestException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        } catch (RuntimeException e) {
            return ResponseEntity.internalServerError().body(Map.of("error", e.getMessage()));
        }
    }
}

