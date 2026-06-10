package dev.dumbanov.fociumapi.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Entity
@Table
@NoArgsConstructor
@Getter
@Setter
public class Achievement {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID uuid;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String description;

    private Integer rewardPoints;

    @ManyToMany(mappedBy = "achievements", cascade = CascadeType.PERSIST)
    private Set<Profile> profiles = new HashSet<>();

    @OneToMany(mappedBy = "achievement", cascade = CascadeType.PERSIST)
    private Set<AchievementPrerequisite> prerequisites;
}
