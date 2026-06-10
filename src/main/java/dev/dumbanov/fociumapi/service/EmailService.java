package dev.dumbanov.fociumapi.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.MailException;
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


    String buildProfileActivationEmail(String email, String link) {
        return """
            Hello,

            Welcome to Focium!

            Your account (%s) has been created and is waiting for activation.

            Please use the link below to activate your account:
            %s

            If you did not create this account, you can safely ignore this email.

            Best regards,
            The Focium Team
            """.formatted(email, link);
    }
    @SuppressWarnings("unused")
    public void sendProfileActivationEmail(String email, UUID code) throws MailException {
        String link = frontendUrl + "/activate-account?code=" + code;
        String content = buildProfileActivationEmail(email, link);

        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(email);
        message.setFrom("fociumapp@gmail.com");
        message.setSubject("Activate your Focium account");
        message.setText(content);
        mailSender.send(message);
    }
}
