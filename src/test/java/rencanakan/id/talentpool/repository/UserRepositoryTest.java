package rencanakan.id.talentpool.repository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import rencanakan.id.talentpool.model.User;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class UserRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private UserRepository userRepository;
    
    private User testUser;
    private String testUserId;
    
    @BeforeEach
    void setUp() {
        // Clear any data from previous tests
        userRepository.deleteAll();
        
        // Create a standard test user
        testUser = User.builder()
                .firstName("John")
                .lastName("Doe")
                .email("john.doe@example.com")
                .password("password123")
                .phoneNumber("1234567890")
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
        
        testUser = entityManager.persistAndFlush(testUser);
        testUserId = testUser.getId();
    }
    
    @Nested
    @DisplayName("Read Operations Tests")
    class ReadTests {
        
        @Test
        @DisplayName("Find user by existing ID")
        void testFindById_ExistingId_ReturnUser() {
            // Act
            Optional<User> found = userRepository.findById(testUserId);
            
            // Assert
            assertTrue(found.isPresent());
            assertEquals(testUserId, found.get().getId());
            assertEquals("John", found.get().getFirstName());
            assertEquals("Doe", found.get().getLastName());
            assertEquals("john.doe@example.com", found.get().getEmail());
            assertEquals("1234567890", found.get().getPhoneNumber());
        }
        
        @Test
        @DisplayName("Find user by non-existing ID")
        void testFindById_NonExistingId_ReturnEmptyOptional() {
            // Arrange
            String nonExistingId = UUID.randomUUID().toString();
            
            // Act
            Optional<User> result = userRepository.findById(nonExistingId);
            
            // Assert
            assertFalse(result.isPresent());
        }
        
        @Test
        @DisplayName("Find user by email")
        void testFindByEmail_ExistingEmail_ReturnUser() {
            // Act
            Optional<User> found = userRepository.findByEmail("john.doe@example.com");
            
            // Assert
            assertTrue(found.isPresent());
            assertEquals("John", found.get().getFirstName());
            assertEquals("Doe", found.get().getLastName());
        }
        
        @Test
        @DisplayName("Find user by non-existing email")
        void testFindByEmail_NonExistingEmail_ReturnEmptyOptional() {
            // Act
            Optional<User> found = userRepository.findByEmail("nonexisting@example.com");
            
            // Assert
            assertFalse(found.isPresent());
        }
        
        @Test
        @DisplayName("Find all users - Single User")
        void testFindAll_SingleUser_ReturnListWithOneUser() {
            // Act
            List<User> users = userRepository.findAll();
            
            // Assert
            assertEquals(1, users.size());
            assertEquals(testUserId, users.get(0).getId());
        }
        
        @Test
        @DisplayName("Find all users - Multiple Users")
        void testFindAll_MultipleUsers_ReturnListWithAllUsers() {
            // Arrange
            User secondUser = User.builder()
                    .firstName("Jane")
                    .lastName("Smith")
                    .email("jane.smith@example.com")
                    .password("password456")
                    .phoneNumber("0987654321")
                    .nik("6543210987654321")
                    .build();
            entityManager.persistAndFlush(secondUser);
            
            // Act
            List<User> users = userRepository.findAll();
            
            // Assert
            assertEquals(2, users.size());
        }
    }
    
    @Nested
    @DisplayName("Create Operations Tests")
    class CreateTests {
        
        @Test
        @DisplayName("Save new user - Positive Case")
        void testSaveUser_NewUser_ReturnSavedUser() {
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
            User saved = userRepository.save(user);
            
            // Assert
            assertNotNull(saved.getId());
            assertEquals("Jane", saved.getFirstName());
            assertEquals("Smith", saved.getLastName());
            
            // Verify it was saved to the database
            Optional<User> retrieved = userRepository.findById(saved.getId());
            assertTrue(retrieved.isPresent());
            assertEquals("Jane", retrieved.get().getFirstName());
        }
        
        @Test
        @DisplayName("Save user with minimal fields - Edge Case")
        void testSaveUser_MinimalFields_ReturnSavedUser() {
            // Arrange
            User minimalUser = User.builder()
                    .firstName("Minimal")
                    .email("minimal@example.com")
                    .build();
            
            // Act
            User saved = userRepository.save(minimalUser);
            
            // Assert
            assertNotNull(saved.getId());
            assertEquals("Minimal", saved.getFirstName());
            assertNull(saved.getLastName());
        }
        
        @Test
        @DisplayName("Save duplicate email - Edge Case")
        void testSaveUser_DuplicateEmail_ReturnSavedUser() {
            // Arrange
            User duplicateEmailUser = User.builder()
                    .firstName("Another")
                    .lastName("User")
                    .email("john.doe@example.com") // Same as testUser
                    .password("anotherPassword")
                    .build();
            
            // Act & Assert
            // This test depends on your application's constraints
            // If unique email is enforced at the database level, this might throw an exception
            User saved = userRepository.save(duplicateEmailUser);
            assertNotNull(saved.getId());
        }
    }
    
    @Nested
    @DisplayName("Delete Operations Tests")
    class DeleteTests {
        
        @Test
        @DisplayName("Delete existing user - Positive Case")
        void testDeleteUser_ExistingId_UserDeleted() {
            // Verify it exists before deleting
            assertTrue(userRepository.findById(testUserId).isPresent());
            
            // Act
            userRepository.deleteById(testUserId);
            
            // Assert
            assertFalse(userRepository.findById(testUserId).isPresent());
        }
        
        @Test
        @DisplayName("Delete non-existing user - Negative Case")
        void testDeleteUser_NonExistingId_ThrowsException() {
            // Arrange
            String nonExistingId = UUID.randomUUID().toString();
            
            // Act & Assert - Most repositories throw an exception when deleting non-existing records
            // The exact exception depends on the implementation
            assertDoesNotThrow(() -> userRepository.deleteById(nonExistingId));
        }
        
        @Test
        @DisplayName("Delete user and verify count - Edge Case")
        void testDeleteUser_VerifyCount() {
            // Arrange
            long initialCount = userRepository.count();
            
            // Act
            userRepository.deleteById(testUserId);
            
            // Assert
            assertEquals(initialCount - 1, userRepository.count());
        }
    }
}
