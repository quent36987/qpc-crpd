package back.app.utils;

import io.swagger.v3.oas.annotations.Hidden;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;

import java.util.Properties;

@Configuration
@Hidden
public class EmailConfig {

    @Value("${mail.smtp.host}")
    private String host;

    @Value("${mail.smtp.port}")
    private int port;

    @Value("${mail.smtp.username}")
    private String username;

    @Value("${mail.smtp.password}")
    private String password;

    @Value("${mail.smtp.auth}")
    private boolean auth;

    @Value("${mail.smtp.starttls.enable}")
    private boolean starttlsEnable;

    @Bean
    public JavaMailSender mailSender() {
        JavaMailSenderImpl mailSender = new JavaMailSenderImpl();
        mailSender.setHost(host);       // ex. "pro3.mail.ovh.net"
        mailSender.setPort(port);       // 587 pour STARTTLS
        mailSender.setUsername(username);
        mailSender.setPassword(password);

        Properties props = mailSender.getJavaMailProperties();
        props.put("mail.transport.protocol",       "smtp");
        props.put("mail.smtp.auth",               "true");
        props.put("mail.smtp.starttls.enable",    "true");
        props.put("mail.smtp.starttls.required",  "true");
        props.put("mail.smtp.ssl.trust",          host);
        props.put("mail.smtp.auth.mechanisms",    "LOGIN");     // ou "LOGIN"
//        props.put("mail.debug",                   "true");      // pour obtenir le log complet
        // timeouts (optionnel)
        props.put("mail.smtp.connectiontimeout", "10000"); // 10s
        props.put("mail.smtp.timeout",           "10000");
        props.put("mail.smtp.writetimeout",      "10000");


        return mailSender;
    }
}
