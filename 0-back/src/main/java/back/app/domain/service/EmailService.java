package back.app.domain.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;


@Service
@Slf4j
@RequiredArgsConstructor
public class EmailService {

    private final JavaMailSender mailSender;

    @Value("${mail.smtp.username}")
    private String username;

    @Value("${back.front.url}")
    private String urlFront;

    public void sendEmail(String to, String subject, String message) {
        SimpleMailMessage email = new SimpleMailMessage();
        email.setTo(to);
        email.setSubject(subject);
        email.setText(message);
        email.setFrom(username);

        mailSender.send(email);
        log.info("Email sent to: {}", to);
    }

    @Async("mailExecutor")
    public void sendCompteCreated(String to) {
        String subject = "Hit Pole - Création de compte";

        String message = "Bonjour,\n\n" +
                "Votre compte a été créé avec succès.\n" +
                "Pour définir votre mot de passe, veuillez vous rendre sur le lien suivant :\n" +
                urlFront + "/auth/login\n" +
                "et cliquer sur « Mot de passe oublié ».\n\n" +
                "Cordialement";

        sendEmail(to, subject, message);
    }
}
