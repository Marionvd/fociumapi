package dev.dumbanov.fociumapi.repository;

import dev.dumbanov.fociumapi.entity.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface RoleRepository extends JpaRepository<Role, UUID> {
    boolean existsRoleByName(String name);

    Role findRoleByName(String name);
}
