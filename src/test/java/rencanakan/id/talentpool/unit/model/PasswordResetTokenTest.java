package rencanakan.id.talentpool.unit.model;

import org.junit.jupiter.api.Test;
import rencanakan.id.talentpool.model.PasswordResetToken;

import java.time.LocalDateTime;
import static org.assertj.core.api.Assertions.assertThat;

class PasswordResetTokenTest {

    @Test
    void builder_shouldCreateTokenCorrectly() {
        LocalDateTime now = LocalDateTime.now();
        PasswordResetToken token = PasswordResetToken.builder()
                .id(1L)
                .email("user@example.com")
                .token("abc123")
                .expiryDate(now)
                .used(false)
                .build();

        assertThat(token.getId()).isEqualTo(1L);
        assertThat(token.getEmail()).isEqualTo("user@example.com");
        assertThat(token.getToken()).isEqualTo("abc123");
        assertThat(token.getExpiryDate()).isEqualTo(now);
        assertThat(token.isUsed()).isFalse();
    }

    @Test
    void setters_shouldUpdateFieldsCorrectly() {
        PasswordResetToken token = new PasswordResetToken();
        token.setId(2L);
        token.setEmail("test@example.com");
        token.setToken("xyz789");
        token.setExpiryDate(LocalDateTime.of(2025, 5, 12, 10, 0));
        token.setUsed(true);

        assertThat(token.getId()).isEqualTo(2L);
        assertThat(token.getEmail()).isEqualTo("test@example.com");
        assertThat(token.getToken()).isEqualTo("xyz789");
        assertThat(token.getExpiryDate()).isEqualTo(LocalDateTime.of(2025, 5, 12, 10, 0));
        assertThat(token.isUsed()).isTrue();
    }
}
