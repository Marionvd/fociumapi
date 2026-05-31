package dev.dumbanov.fociumapi.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.MailSender;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class EmailService {
    private final MailSender mailSender;
    private final String frontendUrl;

    public EmailService(MailSender mailSender, @Value("${app.frontend.url}") String frontendUrl) {
        this.mailSender = mailSender;
        this.frontendUrl = frontendUrl;
    }

    String buildResetPasswordTemplate(String email, String link) {
        return """
            Hello,

            We received a request to reset the password for your Focium account (%s).

            If you made this request, please use the link below to set a new password:
            %s

            If you did not request a password reset, you can safely ignore this email.
            Your password will remain unchanged.

            Best regards,
            The Focium Team
            """.formatted(email, link);
    }

    public void sendPasswordResetEmail(String email, UUID code) {
        String link = frontendUrl + "/reset-password?code=" + code;
        String content = buildResetPasswordTemplate(email, link);

        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(email);
        message.setFrom("fociumapp@gmail.com");
        message.setSubject("Reset Password for Focium account");
        message.setText(content);
        mailSender.send(message);
    }
}
