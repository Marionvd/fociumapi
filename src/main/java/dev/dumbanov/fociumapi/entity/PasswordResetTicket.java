package dev.dumbanov.fociumapi.entity;

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
public class PasswordResetTicket {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID uuid;

    @Column
    private LocalDateTime expiresAt = LocalDateTime.now().plusHours(2);

    @Column
    private Boolean used = false;
    
    @ManyToOne
    private Profile target;
}
