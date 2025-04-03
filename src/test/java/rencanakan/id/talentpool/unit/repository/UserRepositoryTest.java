package rencanakan.id.talentpool.unit.repository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.dao.DataIntegrityViolationException;

import rencanakan.id.talentpool.model.User;
import rencanakan.id.talentpool.repository.UserRepository;

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
        userRepository.deleteAll();
        
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
            Optional<User> found = userRepository.findById(testUserId);
            
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
            String nonExistingId = UUID.randomUUID().toString();
            
            Optional<User> result = userRepository.findById(nonExistingId);
            
            assertFalse(result.isPresent());
        }
        
        @Test
        @DisplayName("Find user by email")
        void testFindByEmail_ExistingEmail_ReturnUser() {
            Optional<User> found = userRepository.findByEmail("john.doe@example.com");
            
            assertTrue(found.isPresent());
            assertEquals("John", found.get().getFirstName());
            assertEquals("Doe", found.get().getLastName());
        }
        
        @Test
        @DisplayName("Find user by non-existing email")
        void testFindByEmail_NonExistingEmail_ReturnEmptyOptional() {
            Optional<User> found = userRepository.findByEmail("nonexisting@example.com");
            
            assertFalse(found.isPresent());
        }
        
        @Test
        @DisplayName("Find all users - Single User")
        void testFindAll_SingleUser_ReturnListWithOneUser() {
            List<User> users = userRepository.findAll();
            
            assertEquals(1, users.size());
            assertEquals(testUserId, users.get(0).getId());
        }
        
        @Test
        @DisplayName("Find all users - Multiple Users")
        void testFindAll_MultipleUsers_ReturnListWithAllUsers() {
            User secondUser = User.builder()
                    .firstName("Jane")
                    .lastName("Smith")
                    .email("jane.smith@example.com")
                    .password("password456")
                    .phoneNumber("0987654321")
                    .nik("6543210987654321")
                    .build();
            entityManager.persistAndFlush(secondUser);
            
            List<User> users = userRepository.findAll();
            
            assertEquals(2, users.size());
        }
    }
    
    @Nested
    @DisplayName("Create Operations Tests")
    class CreateTests {
        
        @Test
        @DisplayName("Save new user")
        void testSaveUser_NewUser_ReturnSavedUser() {
            User user = User.builder()
                    .firstName("Jane")
                    .lastName("Smith")
                    .email("jane.smith@example.com")
                    .password("password456")
                    .phoneNumber("0987654321")
                    .nik("6543210987654321")
                    .build();
            
            User saved = userRepository.save(user);
            
            assertNotNull(saved.getId());
            assertEquals("Jane", saved.getFirstName());
            assertEquals("Smith", saved.getLastName());
            
            Optional<User> retrieved = userRepository.findById(saved.getId());
            assertTrue(retrieved.isPresent());
            assertEquals("Jane", retrieved.get().getFirstName());
        }
        
        @Test
        @DisplayName("Save user with minimal fields")
        void testSaveUser_MinimalFields_ReturnSavedUser() {
            User minimalUser = User.builder()
                    .firstName("Minimal")
                    .email("minimal@example.com")
                    .build();
            
            User saved = userRepository.save(minimalUser);
            
            assertNotNull(saved.getId());
            assertEquals("Minimal", saved.getFirstName());
            assertNull(saved.getLastName());
        }
        
        @Test
        @DisplayName("Save duplicate email")
        void testSaveUser_DuplicateEmail_ReturnSavedUser() {
            User duplicateEmailUser = User.builder()
                    .firstName("Another")
                    .lastName("User")
                    .email("john.doe@example.com")
                    .password("anotherPassword")
                    .build();

            assertThrows(DataIntegrityViolationException.class, () -> {
                userRepository.save(duplicateEmailUser);
                userRepository.flush();
            });
        }
    }
    
    @Nested
    @DisplayName("Delete Operations Tests")
    class DeleteTests {
        
        @Test
        @DisplayName("Delete existing user")
        void testDeleteUser_ExistingId_UserDeleted() {
            assertTrue(userRepository.findById(testUserId).isPresent());
            
            userRepository.deleteById(testUserId);
            
            assertFalse(userRepository.findById(testUserId).isPresent());
        }
        
        @Test
        @DisplayName("Delete non-existing user")
        void testDeleteUser_NonExistingId_ThrowsException() {
            String nonExistingId = UUID.randomUUID().toString();
            long initialCount = userRepository.count();
            
            assertDoesNotThrow(() -> userRepository.deleteById(nonExistingId));
            assertEquals(initialCount, userRepository.count());
        }
        
        @Test
        @DisplayName("Delete user and verify count")
        void testDeleteUser_VerifyCount() {
            long initialCount = userRepository.count();
            
            userRepository.deleteById(testUserId);
            
            assertEquals(initialCount - 1, userRepository.count());
        }
    }
}
