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
                Здравейте,
                
                Получихме заяква за промяна на парола в приложението Focium за %s.
                Ако вие се пратили тази заявка, моля последвайте %s за да продължите с промяната на Вашата парола.
                Ако не се пратили тази заявка, може спокойно да игнорирате този имейл.
                
                Поздрави,
                Екипът на Focium
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
