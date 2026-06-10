package dev.dumbanov.fociumapi.entity;

import dev.dumbanov.fociumapi.entity.types.ShowdownStatus;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table
@NoArgsConstructor
@Getter
@Setter
public class Showdown {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID uuid;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "profile1_uuid", referencedColumnName = "uuid", nullable = false)
    private Profile profile1;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "profile2_uuid", referencedColumnName = "uuid", nullable = false)
    private Profile profile2;

    @Column(nullable = false)
    private Integer profile1Points = 0;

    @Column(nullable = false)
    private Integer profile2Points = 0;

    @Column(nullable = false)
    private LocalDateTime startTime;

    @Column(nullable = false)
    private LocalDateTime endTime;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ShowdownStatus status = ShowdownStatus.ACTIVE;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "winner_uuid", referencedColumnName = "uuid")
    private Profile winner;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();
}

