package rencanakan.id.talentPool.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import java.util.Optional;

import rencanakan.id.talentPool.dto.UserProfileResponseDTO;
import rencanakan.id.talentPool.model.UserProfile;
import rencanakan.id.talentPool.repository.UserProfileRepository;

public class UserProfileServiceImplTest {

    @Mock
    private UserProfileRepository userProfileRepository;

    @InjectMocks
    private UserProfileServiceImpl userProfileService;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testGetById_Success() {
        // Arrange
        String userId = "user123";
        UserProfile userProfile = new UserProfile();
        userProfile.setId(userId);
        userProfile.setFirstName("John");
        userProfile.setLastName("Doe");
        userProfile.setEmail("john.doe@example.com");

        when(userProfileRepository.findById(userId)).thenReturn(Optional.of(userProfile));

        // Act
        UserProfileResponseDTO result = userProfileService.getById(userId);

        // Assert
        assertNotNull(result);
        assertEquals(userId, result.getId());
        assertEquals("John", result.getFirstName());
        assertEquals("Doe", result.getLastName());

        verify(userProfileRepository, times(1)).findById(userId);
    }

    @Test
    public void testGetById_NotFound() {
        String userId = "nonexistent";
        when(userProfileRepository.findById(userId)).thenReturn(Optional.empty());

        UserProfileResponseDTO result = userProfileService.getById(userId);

        assertNull(result);
        verify(userProfileRepository, times(1)).findById(userId);
    }

    @Test
    public void testEdit_Success() {
        String userId = "user123";
        UserProfile userProfile = new UserProfile();
        userProfile.setId(userId);
        userProfile.setFirstName("John");
        userProfile.setLastName("Cena");
        userProfile.setEmail("john.cena12@example.com");
        userProfile.setPassword("password123");
        userProfile.setNik("1234567891011121");

        UserProfile newUserProfile = new UserProfile();
        newUserProfile.setId(userId);
        newUserProfile.setFirstName("Jane");
        newUserProfile.setLastName("Doe");
        newUserProfile.setEmail("jane.doe@example.com");
        newUserProfile.setPhoneNumber("1234567890");
        newUserProfile.setPassword("password1234");
        newUserProfile.setAboutMe("This is me!");
        newUserProfile.setAddress("Jakarta Street");
        newUserProfile.setCurrentLocation("New York");
        newUserProfile.setExperienceYears(6);
        newUserProfile.setJob("Software Engineering");
        newUserProfile.setNik("1234567891011121");
        newUserProfile.setNpwp("01122334456789101231");
        newUserProfile.setPhotoKtp("ktp.jpg");
        newUserProfile.setPhotoNpwp("npwp.jpg");
        newUserProfile.setPhotoIjazah("ijazah.jpg");

        when(userProfileRepository.findById(userId)).thenReturn(Optional.of(userProfile));

        UserProfileResponseDTO editResult = userProfileService.editProfile(userId, newUserProfile);

        assertEquals(userId, editResult.getId());
        assertEquals("Jane", editResult.getFirstName());
        assertEquals("Doe", editResult.getLastName());
        assertEquals("jane.doe@example.com", editResult.getEmail());
    }

    @Test
    public void testEdit_NoChanges() {
        String userId = "user123";
        UserProfile userProfile = new UserProfile();
        userProfile.setId(userId);
        userProfile.setFirstName("John");
        userProfile.setLastName("Doe");
        userProfile.setEmail("john.doe@example.com");

        when(userProfileRepository.findById(userId)).thenReturn(Optional.of(userProfile));

        UserProfileResponseDTO editResult = userProfileService.editProfile(userId, new UserProfile());

        assertEquals(userId, editResult.getId());
        assertEquals("John", editResult.getFirstName());
        assertEquals("Doe", editResult.getLastName());
        assertEquals("john.doe@example.com", editResult.getEmail());
    }

    @Test
    public void testUserProfile_InvalidEmail() {
        UserProfile userProfile = new UserProfile();

        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            userProfile.setEmail("invalid-email");
        });

        assertEquals("Invalid email format", exception.getMessage());
    }

    @Test
    public void testUserProfile_InvalidPassword() {
        UserProfile userProfile = new UserProfile();

        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            userProfile.setPassword("short");
        });

        assertEquals("Password must be at least 8 characters", exception.getMessage());
    }

    @Test
    public void testUserProfile_InvalidNIK() {
        UserProfile userProfile = new UserProfile();

        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            userProfile.setNik("12345678");
        });

        assertEquals("NIK must be exactly 16 digits", exception.getMessage());
    }
}
