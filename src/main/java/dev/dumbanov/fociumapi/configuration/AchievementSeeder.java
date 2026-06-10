package dev.dumbanov.fociumapi.configuration;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import dev.dumbanov.fociumapi.entity.Achievement;
import dev.dumbanov.fociumapi.entity.AchievementPrerequisite;
import dev.dumbanov.fociumapi.entity.types.PrerequisiteType;
import dev.dumbanov.fociumapi.repository.AchievementRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

import java.io.InputStream;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

@Configuration
@RequiredArgsConstructor
public class AchievementSeeder implements ApplicationListener<ContextRefreshedEvent> {
    private final AchievementRepository achievementRepository;
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final Logger logger = LoggerFactory.getLogger(AchievementSeeder.class);

    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {
        try {
            Resource resource = new ClassPathResource("seed/achievements.json");
            if (!resource.exists()) {
                logger.info("No achievements seed file found at classpath:seed/achievements.json");
                return;
            }

            try (InputStream is = resource.getInputStream()) {
                List<AchievementSeed> seeds = objectMapper.readValue(is, new TypeReference<>() {});
                for (AchievementSeed seed : seeds) {
                    if (achievementRepository.findByName(seed.name).isPresent()) {
                        logger.info("Achievement already exists: {}", seed.name);
                        continue;
                    }

                    Achievement achievement = new Achievement();
                    achievement.setName(seed.name);
                    achievement.setDescription(seed.description);
                    achievement.setRewardPoints(seed.rewardPoints);

                    if (seed.prerequisites != null && !seed.prerequisites.isEmpty()) {
                        Set<AchievementPrerequisite> prs = new HashSet<>();
                        for (PrereqSeed p : seed.prerequisites) {
                            AchievementPrerequisite ap = new AchievementPrerequisite();
                            ap.setType(PrerequisiteType.valueOf(p.type));
                            ap.setRequiredAmount(p.requiredAmount);
                            ap.setAchievement(achievement);
                            prs.add(ap);
                        }
                        achievement.setPrerequisites(prs);
                    }

                    achievementRepository.save(achievement);
                    logger.info("Seeded achievement: {}", seed.name);
                }
            }
        } catch (Exception e) {
            logger.warn("Failed to seed achievements: {}", e.getMessage());
        }
    }

    // Local DTOs to bind JSON
    private record AchievementSeed(String name, String description, Integer rewardPoints, List<PrereqSeed> prerequisites) {}
    private record PrereqSeed(String type, Integer requiredAmount) {}
}


