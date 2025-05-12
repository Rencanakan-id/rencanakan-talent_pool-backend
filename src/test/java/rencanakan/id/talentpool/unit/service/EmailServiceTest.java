package rencanakan.id.talentpool.unit.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import rencanakan.id.talentpool.model.PasswordResetToken;
import rencanakan.id.talentpool.repository.PasswordResetTokenRepository;
import rencanakan.id.talentpool.service.EmailServiceImpl;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EmailServiceTest {

    @Mock
    private JavaMailSender mailSender;

    @Mock
    private PasswordResetTokenRepository tokenRepository;

    @InjectMocks
    private EmailServiceImpl emailService;

    @Captor
    private ArgumentCaptor<PasswordResetToken> tokenCaptor;

    @Captor
    private ArgumentCaptor<SimpleMailMessage> mailCaptor;

    @Test
    void itShouldGenerateAndSaveTokenAndSendEmail() {
        // Given
        String email = "user@example.com";

        // When
        emailService.processResetPassword(email);

        // Then
        verify(tokenRepository, times(1)).save(tokenCaptor.capture());
        PasswordResetToken savedToken = tokenCaptor.getValue();

        assertThat(savedToken.getEmail()).isEqualTo(email);
        assertThat(savedToken.getToken()).isNotBlank();
        assertThat(savedToken.getExpiryDate()).isAfter(LocalDateTime.now());
        assertThat(savedToken.isUsed()).isFalse();

        verify(mailSender, times(1)).send(mailCaptor.capture());
        SimpleMailMessage message = mailCaptor.getValue();

        assertThat(message.getTo()).containsExactly(email);
        assertThat(message.getSubject()).contains("Reset Password");
        assertThat(message.getText()).contains("Klik link berikut");
        assertThat(message.getText()).contains(savedToken.getToken());
    }
}
