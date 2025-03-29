package rencanakan.id.talentpool.service;

import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Validator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.lang.reflect.Field;

import rencanakan.id.talentpool.dto.UserResponseDTO;
import rencanakan.id.talentpool.model.User;
import rencanakan.id.talentpool.repository.UserRepository;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private Validator mockValidator;

    @InjectMocks
    private UserServiceImpl userService;

    private String testUserId = "user123";
    private User testUser;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setId(testUserId);
        testUser.setFirstName("John");
        testUser.setLastName("Doe");
        testUser.setEmail("john.doe@example.com");
        testUser.setPassword("password123");
        testUser.setNik("1234567891011121");
    }
    
    @Nested
    class ReadUserTests {
        @Test
        void getById_WithValidId_ReturnsUser() {
            when(userRepository.findById(testUserId)).thenReturn(Optional.of(testUser));

            UserResponseDTO result = userService.getById(testUserId);

            assertNotNull(result);
            assertEquals(testUserId, result.getId());
            assertEquals("John", result.getFirstName());
            assertEquals("Doe", result.getLastName());
            verify(userRepository, times(1)).findById(testUserId);
        }

        @Test
        void getById_WithNonExistentId_ThrowsEntityNotFoundException() {
            String nonExistentId = "nonexistent";
            when(userRepository.findById(nonExistentId)).thenReturn(Optional.empty());

            Exception exception = assertThrows(EntityNotFoundException.class, () -> {
                userService.getById(nonExistentId);
            });
            assertEquals("User with ID " + nonExistentId + " not found", exception.getMessage());
            verify(userRepository, times(1)).findById(nonExistentId);
        }
        
        @Test
        void getById_WithEmptyId_ThrowsEntityNotFoundException() {
            String emptyId = "";
            when(userRepository.findById(emptyId)).thenReturn(Optional.empty());

            Exception exception = assertThrows(EntityNotFoundException.class, () -> {
                userService.getById(emptyId);
            });
            assertEquals("User with ID " + emptyId + " not found", exception.getMessage());
            verify(userRepository, times(1)).findById(emptyId);
        }
        
        @Test
        void findByEmail_WithValidEmail_ReturnsUser() {
            String testEmail = "john.doe@example.com";
            when(userRepository.findByEmail(testEmail)).thenReturn(Optional.of(testUser));

            User result = userService.findByEmail(testEmail);

            assertNotNull(result);
            assertEquals(testUserId, result.getId());
            assertEquals("John", result.getFirstName());
            assertEquals("Doe", result.getLastName());
            assertEquals(testEmail, result.getEmail());
            verify(userRepository, times(1)).findByEmail(testEmail);
        }

        @Test
        void findByEmail_WithNonExistentEmail_ReturnsNull() {
            String nonExistentEmail = "nonexistent@example.com";
            when(userRepository.findByEmail(nonExistentEmail)).thenReturn(Optional.empty());

            User result = userService.findByEmail(nonExistentEmail);

            assertNull(result);
            verify(userRepository, times(1)).findByEmail(nonExistentEmail);
        }
        
        @Test
        void findByEmail_WithEmptyEmail_ReturnsNull() {
            String emptyEmail = "";
            when(userRepository.findByEmail(emptyEmail)).thenReturn(Optional.empty());

            User result = userService.findByEmail(emptyEmail);

            assertNull(result);
            verify(userRepository, times(1)).findByEmail(emptyEmail);
        }
    }
    
    @Nested
    class UpdateUserTests {
        private List<String> preferredLocations;
        private User updatedUserData;
        
        @BeforeEach
        void setUp() {
            preferredLocations = new ArrayList<>();
            preferredLocations.add("Bekasi");
            preferredLocations.add("Depok");
            preferredLocations.add("Bogor");
            
            updatedUserData = new User();
            updatedUserData.setId(testUserId);
            updatedUserData.setFirstName("Jane");
            updatedUserData.setLastName("Doe");
            updatedUserData.setEmail("jane.doe@example.com");
            updatedUserData.setNik("1234567891011121");
        }
        
        @Test
        void editUser_WithValidData_UpdatesSuccessfully() {
            when(userRepository.findById(testUserId)).thenReturn(Optional.of(testUser));
            
            UserResponseDTO editResult = userService.editUser(testUserId, updatedUserData);

            assertNotNull(editResult);
            assertEquals(testUserId, editResult.getId());
            assertEquals("Jane", editResult.getFirstName());
            assertEquals("Doe", editResult.getLastName());
            assertEquals("jane.doe@example.com", editResult.getEmail());
            
            verify(userRepository, times(1)).findById(testUserId);
            verify(userRepository, times(1)).save(any(User.class));
        }

        @Test
        void editUser_WithNoChanges_RetainsOriginalValues() {
            when(userRepository.findById(testUserId)).thenReturn(Optional.of(testUser));
            User emptyUpdateData = new User();

            UserResponseDTO editResult = userService.editUser(testUserId, emptyUpdateData);

            assertEquals(testUserId, editResult.getId());
            assertEquals("John", editResult.getFirstName());
            assertEquals("Doe", editResult.getLastName());
            assertEquals("john.doe@example.com", editResult.getEmail());
            verify(userRepository, times(1)).save(any(User.class));
        }

        @Test
        void editUser_WithInvalidEmail_ThrowsException() throws Exception {
            when(userRepository.findById(testUserId)).thenReturn(Optional.of(testUser));
            User invalidUser = new User();
            Field emailField = User.class.getDeclaredField("email");
            emailField.setAccessible(true);
            emailField.set(invalidUser, "invalid-email");

            Exception exception = assertThrows(IllegalArgumentException.class, () -> {
                userService.editUser(testUserId, invalidUser);
            });
            assertEquals("Failed to update user", exception.getMessage());
        }

        @Test
        void editUser_WithInvalidNIK_ThrowsException() throws Exception {
            when(userRepository.findById(testUserId)).thenReturn(Optional.of(testUser));
            User invalidUser = new User();
            Field nikField = User.class.getDeclaredField("nik");
            nikField.setAccessible(true);
            nikField.set(invalidUser, "invalid-nik");
            invalidUser.setId(testUserId);

            Exception exception = assertThrows(IllegalArgumentException.class, () -> {
                userService.editUser(testUserId, invalidUser);
            });
            assertEquals("Failed to update user", exception.getMessage());
        }

        @Test
        void editUser_WithNonExistentId_ThrowsEntityNotFoundException() {
            when(userRepository.findById("invalid-id")).thenReturn(Optional.empty());
            User invalidUser = new User();

            Exception exception = assertThrows(RuntimeException.class, () -> 
                userService.editUser("invalid-id", invalidUser)
            );
            assertTrue(exception instanceof EntityNotFoundException || exception instanceof RuntimeException);
            assertEquals("User with ID invalid-id not found", exception.getMessage());
        }

        @Test
        void editUser_WithNameTooLong_ThrowsException() throws Exception {
            when(userRepository.findById(testUserId)).thenReturn(Optional.of(testUser));
            User invalidUser = new User();
            invalidUser.setId(testUserId);
            
            String longName = "A".repeat(300);
            Field nameField = User.class.getDeclaredField("firstName");
            nameField.setAccessible(true);
            nameField.set(invalidUser, longName);

            Exception exception = assertThrows(IllegalArgumentException.class, () -> {
                userService.editUser(testUserId, invalidUser);
            });
            assertEquals("Failed to update user", exception.getMessage());
        }
    }
}
