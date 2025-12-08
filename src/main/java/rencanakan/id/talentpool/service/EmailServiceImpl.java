package rencanakan.id.talentpool.service;

import jakarta.mail.internet.MimeMessage;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import rencanakan.id.talentpool.model.PasswordResetToken;
import rencanakan.id.talentpool.repository.PasswordResetTokenRepository;
import rencanakan.id.talentpool.repository.UserRepository;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class EmailServiceImpl implements EmailService {

    private final JavaMailSender mailSender;
    private final PasswordResetTokenRepository tokenRepository;
    private final UserService userService;
    private final UserRepository userRepository;

    @Setter
    @Value("${app.reset-password.base-url}")
    private String resetBaseUrl;

    @Override
    public void sendResetPasswordEmail(String to, String resetLink) {
        String html = loadHtmlTemplate("mail/reset-password.html")
                .replace("{{LINK}}", resetLink);

        userService.findByEmail(to);
        System.out.println("Sending reset password email to " + to);

        try {
            MimeMessage mimeMessage = mailSender.createMimeMessage();
            MimeMessageHelper messageHelper = new MimeMessageHelper(mimeMessage, true, "UTF-8");

            messageHelper.setTo(to);
            messageHelper.setSubject("Reset Password - Talent Pool");
            messageHelper.setText(html, true);

            ClassPathResource logo = new ClassPathResource("mail/logo.png");
            messageHelper.addInline("logoImage", logo);

            mailSender.send(mimeMessage);
        } catch (Exception e) {
            log.error("Failed to send reset password email to {}", to, e);
            throw new RuntimeException("Gagal mengirim email reset password", e);
        }
    }

    @Override
    public void processResetPassword(String email) {
        userRepository.findByEmail(email)
                .orElseThrow(() -> new EntityNotFoundException("User with email" + email + " not found"));

        // Generate token
        String token = UUID.randomUUID().toString();
        LocalDateTime expiry = LocalDateTime.now().plusHours(1);

        PasswordResetToken resetToken = PasswordResetToken.builder()
                .email(email)
                .token(token)
                .expiryDate(expiry)
                .used(false)
                .build();
        tokenRepository.save(resetToken);

        String resetLink = resetBaseUrl + "?token=" + token;
        sendResetPasswordEmail(email, resetLink);
    }

    private String loadHtmlTemplate(String path) {
        try {
            ClassPathResource resource = new ClassPathResource(path);
            byte[] bytes = resource.getInputStream().readAllBytes();
            return new String(bytes, StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new RuntimeException("Failed to load email template: " + path, e);
        }
    }
}