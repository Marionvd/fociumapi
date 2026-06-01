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

    private String pfp="";

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

    @ManyToMany(cascade = CascadeType.PERSIST, fetch = FetchType.EAGER)
    @JoinTable(
            joinColumns = {
                    @JoinColumn(name = "profile_uuid",referencedColumnName = "uuid")
            },
            inverseJoinColumns = {
                    @JoinColumn(name = "shop_item_uuid", referencedColumnName = "uuid")
            }
    )
    private Set<ShopItem> shopItems = new HashSet<>();


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

    public UUID getUuid() {
        return uuid;
    }

    public void setUuid(UUID uuid) {
        this.uuid = uuid;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Integer getPoints() {
        return points;
    }

    public void setPoints(Integer points) {
        this.points = points;
    }

    public Integer getTotalPoints() {
        return totalPoints;
    }

    public void setTotalPoints(Integer totalPoints) {
        this.totalPoints = totalPoints;
    }

    public Integer getLevel() {
        return level;
    }

    public void setLevel(Integer level) {
        this.level = level;
    }

    public Integer getDayStreak() {
        return dayStreak;
    }

    public void setDayStreak(Integer dayStreak) {
        this.dayStreak = dayStreak;
    }

    public String getPfp() {
        return pfp;
    }

    public void setPfp(String pfp) {
        this.pfp = pfp;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getLastLogin() {
        return lastLogin;
    }

    public void setLastLogin(LocalDateTime lastLogin) {
        this.lastLogin = lastLogin;
    }

    public Set<Role> getRoles() {
        return roles;
    }

    public void setRoles(Set<Role> roles) {
        this.roles = roles;
    }

    public Set<Achievement> getAchievements() {
        return achievements;
    }

    public void setAchievements(Set<Achievement> achievements) {
        this.achievements = achievements;
    }

    public Set<ShopItem> getShopItems() {
        return shopItems;
    }

    public void setShopItems(Set<ShopItem> shopItems) {
        this.shopItems = shopItems;
    }
}
