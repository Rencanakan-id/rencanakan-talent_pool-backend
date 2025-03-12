package rencanakan.id.talentpool.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import rencanakan.id.talentpool.model.User;

import java.util.Optional;

@Repository
public interface UserProfileRepository extends JpaRepository<User, String> {
    Optional<User> findByEmail(String email);
}