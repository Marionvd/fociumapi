package dev.dumbanov.fociumapi.service;

import dev.dumbanov.fociumapi.entity.Profile;
import dev.dumbanov.fociumapi.exception.BadRequestException;
import dev.dumbanov.fociumapi.repository.PasswordResetTicketRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

class AuthServiceTests {

    @InjectMocks
    private AuthService authService;

    @Mock
    private PasswordResetTicketRepository passwordResetTicketRepository;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testRegisterUser_Success() {
        // Arrange
        Profile profile = new Profile();
        profile.setEmail("test@example.com");
        when(profileRepository.existsProfileByEmail(anyString())).thenReturn(false);

        // Act
        authService.registerUser(profile);

        // Assert
        verify(profileRepository, times(1)).save(profile);
    }

    @Test
    void testRegisterUser_ExistingEmail() {
        // Arrange
        Profile profile = new Profile();
        profile.setEmail("test@example.com");
        when(profileRepository.existsProfileByEmail(anyString())).thenReturn(true);

        // Act & Assert
        assertThrows(BadRequestException.class, () -> authService.registerUser(profile));
        verify(profileRepository, times(0)).save(profile);
    }

    @Test
    void testLogin_Success() {
        // Arrange
        Profile profile = new Profile();
        profile.setEmail("test@example.com");
        when(profileRepository.findProfileByEmail(anyString())).thenReturn(Optional.of(profile));

        // Act
        String token = authService.login(profile.getEmail(), "password");

        // Assert
        assertNotNull(token);
    }

    @Test
    void testLogin_InvalidEmail() {
        // Arrange
        Profile profile = new Profile();
        profile.setEmail("test@example.com");
        when(profileRepository.findProfileByEmail(anyString())).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(BadRequestException.class, () -> authService.login(profile.getEmail(), "password"));
    }

    @Test
void testLogin_InvalidPassword() {
        // Arrange
        Profile profile = new Profile();
        profile.setEmail("test@example.com");
        when(profileRepository.findProfileByEmail(anyString())).thenReturn(Optional.of(profile));

        // Act & Assert
        assertThrows(BadRequestException.class, () -> authService.login(profile.getEmail(), "wrongpassword"));
    }

    @Test
    void testResetPassword_Success() {
        // Arrange
        Profile profile = new Profile();
        profile.setEmail("test@example.com");
        when(profileRepository.findProfileByEmail(anyString())).thenReturn(Optional.of(profile));

        // Act
        authService.resetPassword(profile.getEmail());

        // Assert
        verify(passwordResetTicketRepository, times(1)).save(any());
    }

    @Test
    void testResetPassword_InvalidEmail() {
        // Arrange
        Profile profile = new Profile();
        profile.setEmail("test@example.com");
        when(profileRepository.findProfileByEmail(anyString())).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(BBadRequestException.class, () -> authService.resetPassword(profile.getEmail()));
    }
}
