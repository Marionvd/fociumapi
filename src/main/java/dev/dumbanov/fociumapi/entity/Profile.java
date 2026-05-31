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

    @Column()
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column()
    private LocalDateTime lastLogin;

    @ManyToMany(cascade = CascadeType.PERSIST, fetch = FetchType.EAGER)
    @JoinTable(
            joinColumns = {
                    @JoinColumn(name = "profile_uuid",referencedColumnName = "uuid"),
                    @JoinColumn(name = "profile_email", referencedColumnName = "email")
            },
            inverseJoinColumns = {
                    @JoinColumn(name = "role_uuid", referencedColumnName = "uuid"),
                    @JoinColumn(name = "role_name", referencedColumnName = "name")
            }
    )
    private Set<Role> roles = new HashSet<>();

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
