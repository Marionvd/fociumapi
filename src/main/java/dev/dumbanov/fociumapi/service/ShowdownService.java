package dev.dumbanov.fociumapi.service;

import dev.dumbanov.fociumapi.entity.Profile;
import dev.dumbanov.fociumapi.entity.Showdown;
import dev.dumbanov.fociumapi.entity.types.ShowdownStatus;
import dev.dumbanov.fociumapi.exception.BadRequestException;
import dev.dumbanov.fociumapi.repository.ShowdownRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ShowdownService {
    private final ShowdownRepository showdownRepository;

    /**
     * Create a new showdown between two profiles.
     * @param profile1 First profile
     * @param profile2 Second profile
     * @param durationMinutes Duration of the showdown in minutes
     * @return Created showdown
     */
    public Showdown createShowdown(Profile profile1, Profile profile2, int durationMinutes) {
        if (profile1.getUuid().equals(profile2.getUuid())) {
            throw new BadRequestException("A profile cannot compete against itself.");
        }

        Showdown showdown = new Showdown();
        showdown.setProfile1(profile1);
        showdown.setProfile2(profile2);
        showdown.setStartTime(LocalDateTime.now());
        showdown.setEndTime(LocalDateTime.now().plusMinutes(durationMinutes));
        showdown.setStatus(ShowdownStatus.ACTIVE);
        showdown.setProfile1Points(0);
        showdown.setProfile2Points(0);

        return showdownRepository.save(showdown);
    }

    /**
     * Add points to a profile in an active showdown.
     * @param showdownUuid Showdown UUID
     * @param profileUuid Profile UUID of the participant
     * @param points Points to add
     */
    public void addPointsToShowdown(UUID showdownUuid, UUID profileUuid, int points) {
        Showdown showdown = showdownRepository.findById(showdownUuid)
                .orElseThrow(() -> new BadRequestException("Showdown not found."));

        if (!showdown.getStatus().equals(ShowdownStatus.ACTIVE)) {
            throw new BadRequestException("Showdown is not active.");
        }

        if (showdown.getEndTime().isBefore(LocalDateTime.now())) {
            throw new BadRequestException("Showdown has ended.");
        }

        if (showdown.getProfile1().getUuid().equals(profileUuid)) {
            showdown.setProfile1Points(showdown.getProfile1Points() + points);
        } else if (showdown.getProfile2().getUuid().equals(profileUuid)) {
            showdown.setProfile2Points(showdown.getProfile2Points() + points);
        } else {
            throw new BadRequestException("Profile is not part of this showdown.");
        }

        showdownRepository.save(showdown);
    }

    /**
     * Complete a showdown and determine the winner.
     * @param showdownUuid Showdown UUID
     */
    public Showdown completeShowdown(UUID showdownUuid) {
        Showdown showdown = showdownRepository.findById(showdownUuid)
                .orElseThrow(() -> new BadRequestException("Showdown not found."));

        if (!showdown.getStatus().equals(ShowdownStatus.ACTIVE)) {
            throw new BadRequestException("Showdown is not active.");
        }

        showdown.setStatus(ShowdownStatus.COMPLETED);

        // Determine winner
        if (showdown.getProfile1Points() > showdown.getProfile2Points()) {
            showdown.setWinner(showdown.getProfile1());
        } else if (showdown.getProfile2Points() > showdown.getProfile1Points()) {
            showdown.setWinner(showdown.getProfile2());
        }
        // If tied, winner remains null

        return showdownRepository.save(showdown);
    }

    /**
     * Get a showdown by UUID.
     * @param uuid Showdown UUID
     * @return Showdown or null if not found
     */
    public Showdown getShowdown(UUID uuid) {
        return showdownRepository.findById(uuid).orElse(null);
    }

    /**
     * Check if a showdown is expired and complete it if needed.
     * @param showdownUuid Showdown UUID
     */
    public void checkAndCompleteExpiredShowdown(UUID showdownUuid) {
        Showdown showdown = showdownRepository.findById(showdownUuid)
                .orElseThrow(() -> new BadRequestException("Showdown not found."));

        if (showdown.getStatus().equals(ShowdownStatus.ACTIVE) && LocalDateTime.now().isAfter(showdown.getEndTime())) {
            completeShowdown(showdownUuid);
        }
    }
}

