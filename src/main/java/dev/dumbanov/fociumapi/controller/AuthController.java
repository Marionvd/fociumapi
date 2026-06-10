package dev.dumbanov.fociumapi.controller;

import com.openai.errors.NotFoundException;
import dev.dumbanov.fociumapi.dto.auth.PasswordLoginInput;
import dev.dumbanov.fociumapi.dto.auth.PasswordRegisterInput;
import dev.dumbanov.fociumapi.dto.auth.PasswordResetInput;
import dev.dumbanov.fociumapi.dto.auth.PasswordResetRequestInput;
import dev.dumbanov.fociumapi.dto.profile.ProfileOutput;
import dev.dumbanov.fociumapi.entity.Profile;
import dev.dumbanov.fociumapi.exception.BadRequestException;
import dev.dumbanov.fociumapi.service.AuthService;
import dev.dumbanov.fociumapi.service.AwarderService;
import dev.dumbanov.fociumapi.service.ProfileActivationTicketService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.MailException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.context.SecurityContextHolderStrategy;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.security.web.context.SecurityContextRepository;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {
    private final Logger logger = LoggerFactory.getLogger(AuthController.class);
    private final AuthService authService;
    private final AwarderService awarderService;
    private final ProfileActivationTicketService profileActivationTicketService;
    private final SecurityContextRepository securityContextRepository =
            new HttpSessionSecurityContextRepository();

    private final SecurityContextHolderStrategy securityContextHolderStrategy =
            SecurityContextHolder.getContextHolderStrategy();

    void createSession(Profile profile, HttpServletRequest request, HttpServletResponse response) {
        Authentication authentication = UsernamePasswordAuthenticationToken.authenticated(
                profile.getEmail(),
                null,
                profile.getAuthorities()
        );

        SecurityContext context = securityContextHolderStrategy.createEmptyContext();
        context.setAuthentication(authentication);

        securityContextHolderStrategy.setContext(context);

        securityContextRepository.saveContext(context, request, response);
    }

    @PostMapping("/password-register")
    public ResponseEntity<?> register(@Valid @RequestBody PasswordRegisterInput input) {
        try {
            Profile profile = authService.register(input.email(), input.password());

            return ResponseEntity.created(URI.create("/api/profiles/" + profile.getUuid()))
                    .body(Map.of(
                            "message", "Account created. Please check your email to activate your account.",
                            "profile", ProfileOutput.of(profile)
                    ));
        } catch (BadRequestException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        } catch (RuntimeException e) {
            return ResponseEntity.internalServerError().body(Map.of("error", e.getMessage()));
        }
    }

    @PostMapping("/password-login")
    public ResponseEntity<?> login(HttpServletRequest request, HttpServletResponse response, @Valid @RequestBody PasswordLoginInput input) {
        try {
            awarderService.manageDayStreak(input.email());
            Profile profile = authService.login(input.email(), input.password());
            createSession(profile, request, response);
            return ResponseEntity.ok(ProfileOutput.of(profile));
        } catch (UsernameNotFoundException e) {
            return ResponseEntity.status(404).body(Map.of("error", e.getMessage()));
        } catch (DisabledException e) {
            return ResponseEntity.status(403).body(Map.of("error", e.getMessage()));
        } catch (BadRequestException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        } catch (RuntimeException e) {
            return ResponseEntity.internalServerError().body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/activate")
    public ResponseEntity<?> activate(@RequestParam String code) {
        try {
            profileActivationTicketService.activate(java.util.UUID.fromString(code));
            return ResponseEntity.ok(Map.of("message", "Account activated successfully. You can now log in."));
        } catch (UsernameNotFoundException e) {
            return ResponseEntity.status(404).body(Map.of("error", e.getMessage()));
        } catch (BadRequestException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", "Invalid activation code."));
        } catch (RuntimeException e) {
            return ResponseEntity.internalServerError().body(Map.of("error", e.getMessage()));
        }
    }

    @PostMapping("/request-password-reset")
    public ResponseEntity<?> requestPasswordReset(@Valid @RequestBody PasswordResetRequestInput input) {
        try {
            authService.requestPasswordResetTicket(input.email());
            return ResponseEntity.ok(Map.of("message", "Password reset email sent successfully."));
        } catch (NotFoundException e) {
            return ResponseEntity.status(404).body(Map.of("error", e.getMessage()));
        } catch (BadRequestException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        } catch (MailException e) {
            logger.warn(e.getMessage());
            return ResponseEntity.internalServerError().body(Map.of("error", "Failed to send password reset email. Please try again later."));
        } catch (RuntimeException e) {
            return ResponseEntity.internalServerError().body(Map.of("error", e.getMessage()));
        }
    }

    @PostMapping("/reset-password")
    public ResponseEntity<?> resetPassword(HttpServletRequest request, HttpServletResponse response, @RequestParam String code, @Valid @RequestBody PasswordResetInput input) {
        try {
            var profile = authService.resetPassword(code, input.newPassword());

            createSession(profile, request, response);
            return ResponseEntity.ok(Map.of("message", "Password was reset successfully"));
        } catch (NotFoundException e) {
            return ResponseEntity.status(404).body(Map.of("error", e.getMessage()));
        } catch (BadRequestException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        } catch (RuntimeException e) {
            return ResponseEntity.internalServerError().body(Map.of("error", e.getMessage()));
        }
    }
}
