package rencanakan.id.talentpool.service;

import jakarta.validation.Validator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

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

    @Captor
    private ArgumentCaptor<User> userCaptor;

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
            // Arrange
            when(userRepository.findById(testUserId)).thenReturn(Optional.of(testUser));

            // Act
            UserResponseDTO result = userService.getById(testUserId);

            // Assert
            assertNotNull(result);
            assertEquals(testUserId, result.getId());
            assertEquals("John", result.getFirstName());
            assertEquals("Doe", result.getLastName());
            verify(userRepository, times(1)).findById(testUserId);
        }

        @Test
        void getById_WithNonExistentId_ReturnsNull() {
            // Arrange
            String nonExistentId = "nonexistent";
            when(userRepository.findById(nonExistentId)).thenReturn(Optional.empty());

            // Act
            UserResponseDTO result = userService.getById(nonExistentId);

            // Assert
            assertNull(result);
            verify(userRepository, times(1)).findById(nonExistentId);
        }
        
        @Test
        void getById_WithEmptyId_ReturnsNull() {
            // Arrange
            String emptyId = "";
            when(userRepository.findById(emptyId)).thenReturn(Optional.empty());

            // Act
            UserResponseDTO result = userService.getById(emptyId);

            // Assert
            assertNull(result);
            verify(userRepository, times(1)).findById(emptyId);
        }
        
        @Test
        void findByEmail_WithValidEmail_ReturnsUser() {
            // Arrange
            String testEmail = "john.doe@example.com";
            when(userRepository.findByEmail(testEmail)).thenReturn(Optional.of(testUser));

            // Act
            User result = userService.findByEmail(testEmail);

            // Assert
            assertNotNull(result);
            assertEquals(testUserId, result.getId());
            assertEquals("John", result.getFirstName());
            assertEquals("Doe", result.getLastName());
            assertEquals(testEmail, result.getEmail());
            verify(userRepository, times(1)).findByEmail(testEmail);
        }

        @Test
        void findByEmail_WithNonExistentEmail_ReturnsNull() {
            // Arrange
            String nonExistentEmail = "nonexistent@example.com";
            when(userRepository.findByEmail(nonExistentEmail)).thenReturn(Optional.empty());

            // Act
            User result = userService.findByEmail(nonExistentEmail);

            // Assert
            assertNull(result);
            verify(userRepository, times(1)).findByEmail(nonExistentEmail);
        }
        
        @Test
        void findByEmail_WithEmptyEmail_ReturnsNull() {
            // Arrange
            String emptyEmail = "";
            when(userRepository.findByEmail(emptyEmail)).thenReturn(Optional.empty());

            // Act
            User result = userService.findByEmail(emptyEmail);

            // Assert
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
            // Setup common test data for update tests
            preferredLocations = new ArrayList<>();
            preferredLocations.add("Bekasi");
            preferredLocations.add("Depok");
            preferredLocations.add("Bogor");
            
            updatedUserData = new User();
            updatedUserData.setId(testUserId);
            updatedUserData.setFirstName("Jane");
            updatedUserData.setLastName("Doe");
            updatedUserData.setEmail("jane.doe@example.com");
            updatedUserData.setPhoneNumber("1234567890");
            updatedUserData.setPassword("password1234");
            updatedUserData.setAboutMe("This is me!");
            updatedUserData.setCurrentLocation("New York");
            updatedUserData.setExperienceYears(6);
            updatedUserData.setPreferredLocations(preferredLocations);
            updatedUserData.setSkkLevel("Professional");
            updatedUserData.setPhoto("photo.jpg");
            updatedUserData.setNik("1234567891011121");
            updatedUserData.setNpwp("01122334456789101231");
            updatedUserData.setPhotoKtp("ktp.jpg");
            updatedUserData.setPhotoNpwp("npwp.jpg");
            updatedUserData.setPhotoIjazah("ijazah.jpg");
            updatedUserData.setSkill("Cooking, Mewing");
            
            // Removing the unnecessary stubbing from here
        }
        
        @Test
        void editUser_WithValidData_UpdatesSuccessfully() {
            // Arrange
            when(userRepository.findById(testUserId)).thenReturn(Optional.of(testUser));
            
            // Act
            UserResponseDTO editResult = userService.editUser(testUserId, updatedUserData);

            // Assert
            assertNotNull(editResult);
            assertEquals(testUserId, editResult.getId());
            assertEquals("Jane", editResult.getFirstName());
            assertEquals("Doe", editResult.getLastName());
            assertEquals("jane.doe@example.com", editResult.getEmail());
            
            // Verify the repository was called to find the user
            verify(userRepository, times(1)).findById(testUserId);
        }

        @Test
        void editUser_WithNoChanges_RetainsOriginalValues() {
            // Arrange
            when(userRepository.findById(testUserId)).thenReturn(Optional.of(testUser));
            User emptyUpdateData = new User();

            // Act
            UserResponseDTO editResult = userService.editUser(testUserId, emptyUpdateData);

            // Assert
            assertEquals(testUserId, editResult.getId());
            assertEquals("John", editResult.getFirstName());
            assertEquals("Doe", editResult.getLastName());
            assertEquals("john.doe@example.com", editResult.getEmail());
        }

        @Test
        void editUser_WithInvalidEmail_ThrowsException() throws Exception {
            // Arrange
            when(userRepository.findById(testUserId)).thenReturn(Optional.of(testUser));
            User invalidUser = new User();
            Field emailField = User.class.getDeclaredField("email");
            emailField.setAccessible(true);
            emailField.set(invalidUser, "invalid-email");

            // Act & Assert
            Exception exception = assertThrows(IllegalArgumentException.class, () -> {
                userService.editUser(testUserId, invalidUser);
            });
            assertEquals("Failed to update user", exception.getMessage());
        }

        @Test
        void editUser_WithShortPassword_ThrowsException() throws Exception {
            // Arrange
            when(userRepository.findById(testUserId)).thenReturn(Optional.of(testUser));
            User invalidUser = new User();
            Field passwordField = User.class.getDeclaredField("password");
            passwordField.setAccessible(true);
            passwordField.set(invalidUser, "pass");
            invalidUser.setId(testUserId);

            // Act & Assert
            Exception exception = assertThrows(IllegalArgumentException.class, () -> {
                userService.editUser(testUserId, invalidUser);
            });
            assertEquals("Failed to update user", exception.getMessage());
        }

        @Test
        void editUser_WithInvalidNIK_ThrowsException() throws Exception {
            // Arrange
            when(userRepository.findById(testUserId)).thenReturn(Optional.of(testUser));
            User invalidUser = new User();
            Field nikField = User.class.getDeclaredField("nik");
            nikField.setAccessible(true);
            nikField.set(invalidUser, "invalid-nik");
            invalidUser.setId(testUserId);

            // Act & Assert
            Exception exception = assertThrows(IllegalArgumentException.class, () -> {
                userService.editUser(testUserId, invalidUser);
            });
            assertEquals("Failed to update user", exception.getMessage());
        }

        @Test
        void editUser_WithNonExistentId_ReturnsNull() {
            // Arrange
            when(userRepository.findById("invalid-id")).thenReturn(Optional.empty());

            // Act
            UserResponseDTO editResult = userService.editUser("invalid-id", new User());

            // Assert
            assertNull(editResult);
        }

        @Test
        void editUser_WithNameTooLong_ThrowsException() throws Exception {
            // Arrange
            when(userRepository.findById(testUserId)).thenReturn(Optional.of(testUser));
            User invalidUser = new User();
            invalidUser.setId(testUserId);
            
            String longName = "A".repeat(300);
            Field nameField = User.class.getDeclaredField("firstName");
            nameField.setAccessible(true);
            nameField.set(invalidUser, longName);

            // Act & Assert
            Exception exception = assertThrows(IllegalArgumentException.class, () -> {
                userService.editUser(testUserId, invalidUser);
            });
            assertEquals("Failed to update user", exception.getMessage());
        }
    }
}
