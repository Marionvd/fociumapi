package dev.dumbanov.fociumapi.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDateTime;
import java.util.*;

@Entity
@Table
@NoArgsConstructor
@Getter
@Setter
public class Profile implements UserDetails {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID uuid;

    @Column(nullable = false, unique = true)
    private String email;

    @Column()
    private String password;

    private Integer points = 0;
    private Integer totalPoints=0;

    private Integer level = 0;
    private Integer dayStreak = 1;

    @Column()
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column()
    private LocalDateTime lastLogin;

    @ManyToMany(cascade = CascadeType.PERSIST, fetch = FetchType.EAGER)
    @JoinTable(
            joinColumns = {
                    @JoinColumn(name = "profile_uuid",referencedColumnName = "uuid")
            },
            inverseJoinColumns = {
                    @JoinColumn(name = "role_uuid", referencedColumnName = "uuid")
            }
    )
    private Set<Role> roles = new HashSet<>();

    @ManyToMany(cascade = CascadeType.PERSIST, fetch = FetchType.EAGER)
    @JoinTable(
            joinColumns = {
                    @JoinColumn(name = "profile_uuid",referencedColumnName = "uuid")
            },
            inverseJoinColumns = {
                    @JoinColumn(name = "achievement_uuid", referencedColumnName = "uuid")
            }
    )
    private Set<Achievement> achievements = new HashSet<>();

    @Override
    public @NonNull Collection<? extends GrantedAuthority> getAuthorities() {
        return roles;
    }

    @Override
    public @Nullable String getPassword() {
        return password;
    }

    @Override
    public @NonNull String getUsername() {
        return email;
    }
}
