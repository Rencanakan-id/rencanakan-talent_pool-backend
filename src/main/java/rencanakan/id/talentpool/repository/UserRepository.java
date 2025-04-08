package rencanakan.id.talentpool.repository;

import jakarta.validation.constraints.NotBlank;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import rencanakan.id.talentpool.model.User;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, String> {
    Optional<User> findByEmail(String email);
    Optional<User> findByNik(String nik);
    Optional<User> findByNpwp(String npwp);
    Optional<User> findByPhoneNumber(String phoneNumber);
}