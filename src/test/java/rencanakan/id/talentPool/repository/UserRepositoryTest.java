package rencanakan.id.talentPool.repository;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import rencanakan.id.talentPool.model.User;

import java.util.Arrays;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
public class UserRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private UserProfileRepository userProfileRepository;

    @Test
    void testFindById_ExistingId_ReturnUserProfile() {
        // Arrange
        User user = User.builder()
                .firstName("John")
                .lastName("Doe")
                .email("john.doe@example.com")
                .password("password123")
                .phoneNumber("1234567890")
                .address("123 Main St")
                .job("Software Engineer")
                .photo("profile.jpg")
                .aboutMe("About me text")
                .nik("1234567890123456")
                .npwp("12.345.678.9-012.345")
                .photoKtp("ktp.jpg")
                .photoNpwp("npwp.jpg")
                .photoIjazah("ijazah.jpg")
                .experienceYears(5)
                .skkLevel("Intermediate")
                .currentLocation("Jakarta")
                .preferredLocations(Arrays.asList("Jakarta", "Bandung"))
                .skill("Java, Spring Boot")
                .build();

        entityManager.persist(user);
        entityManager.flush();
        
        String id = user.getId();

        // Act
        Optional<User> found = userProfileRepository.findById(id);

        // Assert
        assertTrue(found.isPresent());
        assertEquals(id, found.get().getId());
        assertEquals("John", found.get().getFirstName());
        assertEquals("Doe", found.get().getLastName());
        assertEquals("john.doe@example.com", found.get().getEmail());
        assertEquals("1234567890", found.get().getPhoneNumber());
    }

    @Test
    void testFindById_NonExistingId_ReturnEmptyOptional() {
        // Arrange
        String nonExistingId = UUID.randomUUID().toString();

        // Act
        Optional<User> result = userProfileRepository.findById(nonExistingId);

        // Assert
        assertFalse(result.isPresent());
    }
    
    @Test
    void testSaveUserProfile() {
        // Arrange
        User user = User.builder()
                .firstName("Jane")
                .lastName("Smith")
                .email("jane.smith@example.com")
                .password("password456")
                .phoneNumber("0987654321")
                .nik("6543210987654321")
                .build();

        // Act
        User saved = userProfileRepository.save(user);

        // Assert
        assertNotNull(saved.getId());
        assertEquals("Jane", saved.getFirstName());
        assertEquals("Smith", saved.getLastName());
        
        // Verify it was saved to the database
        Optional<User> retrieved = userProfileRepository.findById(saved.getId());
        assertTrue(retrieved.isPresent());
        assertEquals("Jane", retrieved.get().getFirstName());
    }
    
    @Test
    void testDeleteUserProfile() {
        // Arrange
        User user = User.builder()
                .firstName("Alex")
                .lastName("Johnson")
                .email("alex.johnson@example.com")
                .password("password789")
                .phoneNumber("1122334455")
                .nik("1122334455667788")
                .build();

        entityManager.persist(user);
        entityManager.flush();
        
        String id = user.getId();
        
        // Verify it exists
        assertTrue(userProfileRepository.findById(id).isPresent());
        
        // Act
        userProfileRepository.deleteById(id);
        
        // Assert
        assertFalse(userProfileRepository.findById(id).isPresent());
    }
    
    @Test
    void testFindByEmail() {
        // Arrange
        User user = User.builder()
                .firstName("Sarah")
                .lastName("Wilson")
                .email("sarah.wilson@example.com")
                .password("password123")
                .phoneNumber("5544332211")
                .nik("8877665544332211")
                .build();

        entityManager.persist(user);
        entityManager.flush();
        
        // Act
        Optional<User> found = userProfileRepository.findByEmail("sarah.wilson@example.com");
        
        // Assert
        assertTrue(found.isPresent());
        assertEquals("Sarah", found.get().getFirstName());
        assertEquals("Wilson", found.get().getLastName());
    }
}
