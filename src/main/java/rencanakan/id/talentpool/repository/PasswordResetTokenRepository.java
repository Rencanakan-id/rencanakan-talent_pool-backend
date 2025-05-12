package rencanakan.id.talentpool.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import rencanakan.id.talentpool.model.PasswordResetToken;

import java.util.Optional;

public interface PasswordResetTokenRepository extends JpaRepository<PasswordResetToken, Long> {
    Optional<PasswordResetToken> findByToken(String token);
    Optional<PasswordResetToken> findByEmailAndUsedIsFalse(String email);
}