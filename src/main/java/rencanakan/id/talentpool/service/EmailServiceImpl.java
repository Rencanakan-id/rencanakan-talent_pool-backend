package rencanakan.id.talentpool.service;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import rencanakan.id.talentpool.model.PasswordResetToken;
import rencanakan.id.talentpool.model.User;
import rencanakan.id.talentpool.repository.PasswordResetTokenRepository;
import rencanakan.id.talentpool.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class EmailServiceImpl implements EmailService {

    private final JavaMailSender mailSender;
    private final PasswordResetTokenRepository tokenRepository;
    private final UserService userService;
    private final UserRepository userRepository;

    @Override
    public void sendResetPasswordEmail(String to, String resetLink) {
        userService.findByEmail(to);
        System.out.println("Sending reset password email to " + to);
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        message.setSubject("Reset Password - Talent Pool");
        message.setText("Klik link berikut untuk reset password Anda:\n" + resetLink + "\n\nJika Anda tidak meminta reset password, abaikan email ini.");
        mailSender.send(message);
    }

    @Override
    public void processResetPassword(String email) {
        userRepository.findByEmail(email)
                .orElseThrow(() -> new EntityNotFoundException("User with email" + email + " not found"));

        // Generate token
        String token = UUID.randomUUID().toString();
        LocalDateTime expiry = LocalDateTime.now().plusHours(1);

        // Simpan token ke DB
        PasswordResetToken resetToken = PasswordResetToken.builder()
                .email(email)
                .token(token)
                .expiryDate(expiry)
                .used(false)
                .build();
        tokenRepository.save(resetToken);

        String resetLink = "https://rencanakanid-stg.netlify.app/reset-password?token=" + token;
        sendResetPasswordEmail(email, resetLink);
    }
}