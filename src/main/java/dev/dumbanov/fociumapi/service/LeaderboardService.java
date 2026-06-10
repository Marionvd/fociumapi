package dev.dumbanov.fociumapi.service;

import dev.dumbanov.fociumapi.entity.Profile;
import dev.dumbanov.fociumapi.repository.ProfileRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class LeaderboardService {
    private final ProfileRepository profileRepository;

    public List<Profile> getLeaderboard(int size, int page) {
        if (size <= 0) size = 10;
        if (page < 0) page = 0;
        Pageable pageable = PageRequest.of(page, size);
        return profileRepository.findAllByOrderByTotalPointsDesc(pageable);
    }
}
