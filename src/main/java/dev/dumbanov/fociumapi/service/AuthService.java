package dev.dumbanov.fociumapi.service;

import dev.dumbanov.fociumapi.entity.Profile;
import dev.dumbanov.fociumapi.entity.Role;
import dev.dumbanov.fociumapi.exception.BadRequestException;
import dev.dumbanov.fociumapi.repository.ProfileRepository;
import dev.dumbanov.fociumapi.repository.RoleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final ProfileRepository profileRepository;
    private final PasswordEncoder passwordEncoder;
    private final RoleRepository roleRepository;

    public Profile register(String email, String password) {
        Role role = new Role();
        if(!roleRepository.existsRoleByName("ROLE_USER")) {
            role.setName("ROLE_USER");
            role = roleRepository.save(role);
        } else {
            role = roleRepository.findRoleByName("ROLE_USER");
        }

        if(profileRepository.existsProfileByEmail(email)) {
            throw new BadRequestException("User with this email already exists. Please try another email or login.");
        }

        Profile profile = new Profile();
        profile.setEmail(email);
        profile.setPassword(passwordEncoder.encode(password));
        profile.getRoles().add(role);

        return profileRepository.save(profile);
    }

    public Profile login(String email, String password) {
        if(!profileRepository.existsProfileByEmail(email)) {
            throw new BadRequestException("User with this email does not exist.");
        }

        Profile profile = profileRepository.findProfileByEmail(email);
        if(passwordEncoder.matches(password, profile.getPassword())) {
            return profile;
        } else {
            throw new BadRequestException("Incorrect password. Please try again.");
        }
    }
}
