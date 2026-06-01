package dev.dumbanov.fociumapi.controller;

import dev.dumbanov.fociumapi.dto.showdown.ShowdownOutput;
import dev.dumbanov.fociumapi.dto.showdownrequest.ShowdownRequestCreateInput;
import dev.dumbanov.fociumapi.dto.showdownrequest.ShowdownRequestOutput;
import dev.dumbanov.fociumapi.entity.Profile;
import dev.dumbanov.fociumapi.exception.BadRequestException;
import dev.dumbanov.fociumapi.service.ProfileService;
import dev.dumbanov.fociumapi.service.ShowdownRequestService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/showdown-requests")
@RequiredArgsConstructor
public class ShowdownRequestController {
    private final ShowdownRequestService showdownRequestService;
    private final ProfileService profileService;



    @PostMapping
    public ResponseEntity<?> sendRequest(@AuthenticationPrincipal String email,
                                         @Valid @RequestBody ShowdownRequestCreateInput input) {
        try {
            var profile = profileService.getProfileByEmail(email);
            var request = showdownRequestService.sendRequest(profile, input.receiverUuid(), input.durationMinutes());
            return ResponseEntity.created(URI.create("/api/showdown-requests/" + request.getUuid()))
                    .body(ShowdownRequestOutput.of(request));
        } catch (BadRequestException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        } catch (RuntimeException e) {
            return ResponseEntity.internalServerError().body(Map.of("error", e.getMessage()));
        }
    }

    @PostMapping("/{uuid}/accept")
    public ResponseEntity<?> acceptRequest(@AuthenticationPrincipal String email,
                                           @PathVariable UUID uuid) {
        try {
            var profile = profileService.getProfileByEmail(email);
            var showdown = showdownRequestService.acceptRequest(profile, uuid);
            return ResponseEntity.created(URI.create("/api/showdowns/" + showdown.getUuid()))
                    .body(ShowdownOutput.of(showdown));
        } catch (BadRequestException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        } catch (RuntimeException e) {
            return ResponseEntity.internalServerError().body(Map.of("error", e.getMessage()));
        }
    }

    @PostMapping("/{uuid}/decline")
    public ResponseEntity<?> declineRequest(@AuthenticationPrincipal String email,
                                            @PathVariable UUID uuid) {
        try {
            var profile = profileService.getProfileByEmail(email);
            showdownRequestService.declineRequest(profile, uuid);
            return ResponseEntity.ok(Map.of("message", "Request declined."));
        } catch (BadRequestException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        } catch (RuntimeException e) {
            return ResponseEntity.internalServerError().body(Map.of("error", e.getMessage()));
        }
    }

    @PostMapping("/{uuid}/cancel")
    public ResponseEntity<?> cancelRequest(@AuthenticationPrincipal String email,
                                           @PathVariable UUID uuid) {
        try {
            var profile = profileService.getProfileByEmail(email);
            showdownRequestService.cancelRequest(profile, uuid);
            return ResponseEntity.ok(Map.of("message", "Request cancelled."));
        } catch (BadRequestException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        } catch (RuntimeException e) {
            return ResponseEntity.internalServerError().body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/incoming")
    public ResponseEntity<?> getIncomingRequests(@AuthenticationPrincipal String email) {
        try {
            var profile = profileService.getProfileByEmail(email);
            List<ShowdownRequestOutput> requests = showdownRequestService.getIncomingRequests(profile)
                    .stream().map(ShowdownRequestOutput::of).toList();
            return ResponseEntity.ok(requests);
        } catch (RuntimeException e) {
            return ResponseEntity.internalServerError().body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/outgoing")
    public ResponseEntity<?> getOutgoingRequests(@AuthenticationPrincipal Profile profile) {
        try {
            List<ShowdownRequestOutput> requests = showdownRequestService.getOutgoingRequests(profile)
                    .stream().map(ShowdownRequestOutput::of).toList();
            return ResponseEntity.ok(requests);
        } catch (RuntimeException e) {
            return ResponseEntity.internalServerError().body(Map.of("error", e.getMessage()));
        }
    }
}
