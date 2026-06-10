package dev.dumbanov.fociumapi.entity;

import dev.dumbanov.fociumapi.entity.types.PrerequisiteType;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Entity
@Table
@NoArgsConstructor
@Getter
@Setter
public class AchievementPrerequisite {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID uuid;

    @Enumerated(EnumType.STRING)
    private PrerequisiteType type;
    private Integer requiredAmount;

    @ManyToOne
    public Achievement achievement;
}
