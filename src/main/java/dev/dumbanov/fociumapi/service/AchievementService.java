package dev.dumbanov.fociumapi.service;

import dev.dumbanov.fociumapi.dto.achievement.AchievementCreateInput;
import dev.dumbanov.fociumapi.dto.achievement.AchievementUpdateInput;
import dev.dumbanov.fociumapi.dto.achievement.PrerequisiteInput;
import dev.dumbanov.fociumapi.entity.Achievement;
import dev.dumbanov.fociumapi.entity.AchievementPrerequisite;
import dev.dumbanov.fociumapi.entity.types.PrerequisiteType;
import dev.dumbanov.fociumapi.exception.BadRequestException;
import dev.dumbanov.fociumapi.repository.AchievementPrerequisiteRepository;
import dev.dumbanov.fociumapi.repository.AchievementRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AchievementService {
    private final AchievementRepository achievementRepository;
    private final AchievementPrerequisiteRepository prerequisiteRepository;

    public Achievement createAchievement(AchievementCreateInput input) {
        if (achievementRepository.findByName(input.name()).isPresent()) {
            throw new BadRequestException("Achievement with this name already exists.");
        }

        Achievement a = new Achievement();
        a.setName(input.name());
        a.setDescription(input.description());
        a.setRewardPoints(input.rewardPoints());

        if (input.prerequisites() != null && !input.prerequisites().isEmpty()) {
            Set<AchievementPrerequisite> prs = new HashSet<>();
            for (PrerequisiteInput p : input.prerequisites()) {
                AchievementPrerequisite ap = new AchievementPrerequisite();
                try {
                    ap.setType(PrerequisiteType.valueOf(p.type()));
                } catch (IllegalArgumentException e) {
                    throw new BadRequestException("Invalid prerequisite type: " + p.type());
                }
                ap.setRequiredAmount(p.requiredAmount());
                ap.setAchievement(a);
                prs.add(ap);
            }
            a.setPrerequisites(prs);
        }

        return achievementRepository.save(a);
    }

    public Achievement updateAchievement(UUID uuid, AchievementUpdateInput input) {
        Achievement a = achievementRepository.findById(uuid).orElseThrow(() -> new BadRequestException("Achievement not found."));

        if (input.name() != null) {
            a.setName(input.name());
        }
        if (input.description() != null) {
            a.setDescription(input.description());
        }
        if (input.rewardPoints() != null) {
            a.setRewardPoints(input.rewardPoints());
        }

        if (input.prerequisites() != null) {
            // remove existing prerequisites
            prerequisiteRepository.deleteByAchievement(a);
            Set<AchievementPrerequisite> prs = new HashSet<>();
            for (PrerequisiteInput p : input.prerequisites()) {
                AchievementPrerequisite ap = new AchievementPrerequisite();
                try {
                    ap.setType(PrerequisiteType.valueOf(p.type()));
                } catch (IllegalArgumentException e) {
                    throw new BadRequestException("Invalid prerequisite type: " + p.type());
                }
                ap.setRequiredAmount(p.requiredAmount());
                ap.setAchievement(a);
                prs.add(ap);
            }
            a.setPrerequisites(prs);
        }

        return achievementRepository.save(a);
    }

    public void deleteAchievement(UUID uuid) {
        Achievement a = achievementRepository.findById(uuid).orElseThrow(() -> new BadRequestException("Achievement not found."));
        prerequisiteRepository.deleteByAchievement(a);
        achievementRepository.delete(a);
    }
}

