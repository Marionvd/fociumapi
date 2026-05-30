package dev.dumbanov.fociumapi.controller;

import dev.dumbanov.fociumapi.dto.auth.PasswordLoginInput;
import dev.dumbanov.fociumapi.dto.auth.PasswordRegisterInput;
import dev.dumbanov.fociumapi.dto.profile.ProfileOutput;
import dev.dumbanov.fociumapi.entity.Profile;
import dev.dumbanov.fociumapi.exception.BadRequestException;
import dev.dumbanov.fociumapi.service.AuthService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.context.SecurityContextHolderStrategy;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.security.web.context.SecurityContextRepository;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {
    private final AuthService authService;
    private final SecurityContextRepository securityContextRepository =
            new HttpSessionSecurityContextRepository();

    private final SecurityContextHolderStrategy securityContextHolderStrategy =
            SecurityContextHolder.getContextHolderStrategy();

    void createSession(Profile profile, HttpServletRequest request, HttpServletResponse response) {
        Authentication authentication = UsernamePasswordAuthenticationToken.authenticated(
                profile,
                null,
                profile.getAuthorities()
        );

        SecurityContext context = securityContextHolderStrategy.createEmptyContext();
        context.setAuthentication(authentication);

        securityContextHolderStrategy.setContext(context);

        securityContextRepository.saveContext(context, request,  response);
    }

    @PostMapping("/password-register")
    public ResponseEntity<?> register(HttpServletRequest request, HttpServletResponse response, @Valid @RequestBody PasswordRegisterInput input) {
        try {
            Profile profile = authService.register(input.email(), input.password());

            createSession(profile, request, response);

            return ResponseEntity.created(URI.create("/api/profiles/"+profile.getUuid()))
                    .body(ProfileOutput.of(profile));
        } catch (BadRequestException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        } catch (RuntimeException e) {
            return ResponseEntity.internalServerError().body(Map.of("error", e.getMessage()));
        }
    }

    @PostMapping("/password-login")
    public ResponseEntity<?> login(HttpServletRequest request, HttpServletResponse response, @Valid @RequestBody PasswordLoginInput input) {
        try {
            Profile profile = authService.login(input.email(), input.password());

            createSession(profile, request, response);
            return ResponseEntity.ok(ProfileOutput.of(profile));
        } catch (BadRequestException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        } catch (RuntimeException e) {
            return ResponseEntity.internalServerError().body(Map.of("error", e.getMessage()));
        }
    }
}
