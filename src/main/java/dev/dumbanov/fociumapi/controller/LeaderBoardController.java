package dev.dumbanov.fociumapi.controller;

import dev.dumbanov.fociumapi.dto.profile.ProfileOutput;
import dev.dumbanov.fociumapi.service.LeaderboardService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/leaderboard")
@RequiredArgsConstructor
public class LeaderBoardController {
	private final LeaderboardService leaderboardService;

	@GetMapping
	public ResponseEntity<?> getLeaderboard(@RequestParam(defaultValue = "0") int page,
											@RequestParam(defaultValue = "20") int size) {
		var profiles = leaderboardService.getLeaderboard(size, page);
		var out = profiles.stream().map(ProfileOutput::of).toList();
		return ResponseEntity.ok(out);
	}
}
