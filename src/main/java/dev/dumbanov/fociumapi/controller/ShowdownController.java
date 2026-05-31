package dev.dumbanov.fociumapi.controller;

import dev.dumbanov.fociumapi.dto.showdown.ShowdownCreateInput;
import dev.dumbanov.fociumapi.dto.showdown.ShowdownOutput;
import dev.dumbanov.fociumapi.entity.Profile;
import dev.dumbanov.fociumapi.entity.Showdown;
import dev.dumbanov.fociumapi.exception.BadRequestException;
import dev.dumbanov.fociumapi.repository.ProfileRepository;
import dev.dumbanov.fociumapi.service.ShowdownService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/showdowns")
@RequiredArgsConstructor
public class ShowdownController {
    private final ShowdownService showdownService;
    private final ProfileRepository profileRepository;

    @PostMapping
    public ResponseEntity<?> createShowdown(@AuthenticationPrincipal Profile profile,
                                            @Valid @RequestBody ShowdownCreateInput input) {
        try {
            Profile opponent = profileRepository.findById(input.profile2Uuid())
                    .orElseThrow(() -> new BadRequestException("Opponent profile not found."));

            Showdown s = showdownService.createShowdown(profile, opponent, input.durationMinutes());
            return ResponseEntity.created(URI.create("/api/showdowns/" + s.getUuid()))
                    .body(ShowdownOutput.of(s));
        } catch (BadRequestException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
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

