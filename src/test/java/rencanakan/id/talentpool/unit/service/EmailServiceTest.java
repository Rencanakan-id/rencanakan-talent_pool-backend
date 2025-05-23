package rencanakan.id.talentpool.unit.service;

import org.junit.jupiter.api.BeforeEach;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import rencanakan.id.talentpool.model.PasswordResetToken;
import rencanakan.id.talentpool.model.User;
import java.util.Optional;
import rencanakan.id.talentpool.repository.PasswordResetTokenRepository;
import rencanakan.id.talentpool.repository.UserRepository;
import rencanakan.id.talentpool.service.EmailServiceImpl;
import rencanakan.id.talentpool.service.UserService;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EmailServiceTest {

    @Mock
    private JavaMailSender mailSender;

    @Mock
    private PasswordResetTokenRepository tokenRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private UserService userService;


    @InjectMocks
    private EmailServiceImpl emailService;

    @Captor
    private ArgumentCaptor<PasswordResetToken> tokenCaptor;

    @Captor
    private ArgumentCaptor<SimpleMailMessage> mailCaptor;

    @BeforeEach
    void setup() {
        // Inject mock base URL for testing
        emailService.setResetBaseUrl("https://mock-reset.com/reset");
    }

    @Test
    void itShouldGenerateAndSaveTokenAndSendEmail() {
        String email = "user@example.com";
        User dummyUser = new User();
        dummyUser.setEmail(email);
        when(userRepository.findByEmail(email)).thenReturn(Optional.of(dummyUser));

        emailService.processResetPassword(email);

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
        assertThat(message.getText()).contains("https://mock-reset.com/reset");
    }

    @Test
    void itShouldThrowWhenUserDoesNotExist() {
        // Given
        String email = "nonexistent@example.com";
        when(userRepository.findByEmail(email)).thenReturn(Optional.empty());

        // When & Then
        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, () -> {
            emailService.processResetPassword(email);
        });

        assertThat(exception.getMessage()).contains(email);
    }
}
