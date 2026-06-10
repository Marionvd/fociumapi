package dev.dumbanov.fociumapi.service;

import dev.dumbanov.fociumapi.entity.PasswordResetTicket;
import dev.dumbanov.fociumapi.entity.Profile;
import dev.dumbanov.fociumapi.entity.Role;
import dev.dumbanov.fociumapi.exception.BadRequestException;
import dev.dumbanov.fociumapi.repository.PasswordResetTicketRepository;
import dev.dumbanov.fociumapi.repository.ProfileRepository;
import dev.dumbanov.fociumapi.repository.RoleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.parameters.P;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final ProfileRepository profileRepository;
    private final PasswordEncoder passwordEncoder;
    private final RoleRepository roleRepository;
    private final PasswordResetTicketRepository passwordResetTicketRepository;
    private final EmailService emailService;

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
        profile.setLastLogin(LocalDateTime.now());

        return profileRepository.save(profile);
    }

    public Profile login(String email, String password) {
        if(!profileRepository.existsProfileByEmail(email)) {
            throw new UsernameNotFoundException("User with this email does not exist.");
        }

        Profile profile = profileRepository.findProfileByEmail(email);
        if(!passwordEncoder.matches(password, profile.getPassword())) {
            throw new BadRequestException("Incorrect password. Please try again.");
        }
        
        profile.setLastLogin(LocalDateTime.now());
        profile = profileRepository.save(profile);
        return profile;
    }

    public void requestPasswordResetTicket(String email) {
        if(!profileRepository.existsProfileByEmail(email)) {
            throw new UsernameNotFoundException("User with this email does not exist.");
        }

        var profile = profileRepository.findProfileByEmail(email);

        var ticket = new PasswordResetTicket();
        ticket.setTarget(profile);
        ticket = passwordResetTicketRepository.save(ticket);
        emailService.sendPasswordResetEmail(profile.getEmail(), ticket.getUuid());
    }

    public Profile resetPassword(String code, String newPassword) {
        if(!passwordResetTicketRepository.existsById(UUID.fromString(code))) {
            throw new UsernameNotFoundException("Invalid password reset code.");
        }

        var ticket = passwordResetTicketRepository.findPasswordResetTicketByUuid(UUID.fromString(code));
        if(ticket.getUsed()||ticket.getExpiresAt().isBefore(LocalDateTime.now())) {
            throw new BadRequestException("This password reset code has expired or has already been used.");
        }
        ticket.setUsed(true);
        var profile = ticket.getTarget();
        profile.setPassword(passwordEncoder.encode(newPassword));

        passwordResetTicketRepository.save(ticket);
        return profileRepository.save(profile);
    }
}
