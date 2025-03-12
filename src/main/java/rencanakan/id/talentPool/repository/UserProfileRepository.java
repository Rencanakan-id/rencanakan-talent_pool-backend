package rencanakan.id.talentpool.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import rencanakan.id.talentpool.model.UserProfile;

import java.util.Optional;

@Repository
public interface UserProfileRepository extends JpaRepository<UserProfile, String> {
    
    /**
     * Find a user profile by email address
     * 
     * @param email the email address to search for
     * @return an Optional containing the user profile if found, or an empty Optional if not found
     */
    Optional<UserProfile> findByEmail(String email);
}