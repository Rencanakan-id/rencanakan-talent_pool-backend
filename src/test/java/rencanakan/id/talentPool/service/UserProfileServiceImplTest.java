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
        String userId = "user123";
        UserProfile userProfile = new UserProfile();
        userProfile.setId(userId);
        userProfile.setFirstName("John");
        userProfile.setLastName("Doe");
        userProfile.setEmail("john.doe@example.com");

        when(userProfileRepository.findById(userId)).thenReturn(Optional.of(userProfile));

        UserProfileResponseDTO result = userProfileService.getById(userId);

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

        UserProfile newUserProfile = new UserProfile();
        newUserProfile.setId(userId);
        newUserProfile.setFirstName("Jane");
        newUserProfile.setLastName("Doe");
        newUserProfile.setEmail("jane.doe@example.com");
        newUserProfile.setPhoto("profile_new.jpg");
        newUserProfile.setPhotoNpwp("npwp_new.jpg");
        newUserProfile.setPhotoKtp("ktp_new.jpg");
        newUserProfile.setPhotoIjazah("diploma_new.pdf");

        when(userProfileRepository.findById(userId)).thenReturn(Optional.of(userProfile));

        UserProfileResponseDTO editResult = userProfileService.editProfile(userId, newUserProfile);

        assertEquals(userId, editResult.getId());
        assertEquals("Jane", editResult.getFirstName());
        assertEquals("Doe", editResult.getLastName());
        assertEquals("jane.doe@example.com", editResult.getEmail());
        assertEquals("profile_new.jpg", editResult.getPhoto());
        assertEquals("npwp_new.jpg", editResult.getPhotoNpwp());
        assertEquals("ktp_new.jpg", editResult.getPhotoKtp());
        assertEquals("diploma_new.pdf", editResult.getPhotoIjazah());
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

        UserProfileResponseDTO editResult = userProfileService.editProfile(userId, userProfile);

        assertEquals(userId, editResult.getId());
        assertEquals("John", editResult.getFirstName());
        assertEquals("Doe", editResult.getLastName());
        assertEquals("john.doe@example.com", editResult.getEmail());
    }

    @Test
    public void testEdit_UserNotFound() {
        String userId = "nonexistent";
        UserProfile newUserProfile = new UserProfile();
        newUserProfile.setFirstName("Jane");

        when(userProfileRepository.findById(userId)).thenReturn(Optional.empty());

        UserProfileResponseDTO editResult = userProfileService.editProfile(userId, newUserProfile);

        assertNull(editResult);
    }

    @Test
    public void testEdit_InvalidEmail() {
        String userId = "user123";
        UserProfile userProfile = new UserProfile();
        userProfile.setId(userId);
        userProfile.setEmail("valid@example.com");

        UserProfile newUserProfile = new UserProfile();
        newUserProfile.setEmail("invalid-email");

        when(userProfileRepository.findById(userId)).thenReturn(Optional.of(userProfile));

        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            userProfileService.editProfile(userId, newUserProfile);
        });

        assertEquals("Invalid email format", exception.getMessage());
    }

    @Test
    public void testEdit_InvalidPhoneNumber() {
        String userId = "user123";
        UserProfile userProfile = new UserProfile();
        userProfile.setId(userId);
        userProfile.setPhoneNumber("08123456789");

        UserProfile newUserProfile = new UserProfile();
        newUserProfile.setPhoneNumber("abcd123");

        when(userProfileRepository.findById(userId)).thenReturn(Optional.of(userProfile));

        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            userProfileService.editProfile(userId, newUserProfile);
        });

        assertEquals("Invalid phone number", exception.getMessage());
    }
}
