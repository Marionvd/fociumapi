package dev.dumbanov.fociumapi.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.Set;
import java.util.UUID;

@Entity
@Table
@NoArgsConstructor
@Getter
@Setter
public class Task {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID uuid;

    @Column(nullable = false)
    private String title;

    private String description = null;

    private LocalDateTime deadline = null;

    private Integer awardedPoints = null;

    @Column(length = 500)
    private String aiFeedback = null;

    @ManyToOne
    private Profile profile;
}
