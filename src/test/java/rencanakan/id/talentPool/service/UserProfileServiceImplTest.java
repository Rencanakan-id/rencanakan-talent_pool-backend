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
        // Set other fields as needed

        when(userProfileRepository.findById(userId)).thenReturn(Optional.of(userProfile));

        // Act
        UserProfileResponseDTO result = userProfileService.getById(userId);

        // Assert
        assertNotNull(result);
        assertEquals(userId, result.getId());
        assertEquals("John", result.getFirstName());
        assertEquals("Doe", result.getLastName());
        // Add additional assertions as needed

        verify(userProfileRepository, times(1)).findById(userId);
    }

    @Test
    public void testGetById_NotFound() {
        // Arrange
        String userId = "nonexistent";
        when(userProfileRepository.findById(userId)).thenReturn(Optional.empty());

        // Act
        UserProfileResponseDTO result = userProfileService.getById(userId);

        // Assert
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
        userProfile.setPassword("password");

        String newFirstName = "Jane";
        String newLastName = "Doe";
        String newEmail = "jane.doe@example.com";
        UserProfile newUserProfile = new UserProfile();
        newUserProfile.setId(userId);
        newUserProfile.setFirstName(newFirstName);
        newUserProfile.setLastName(newLastName);
        newUserProfile.setEmail(newEmail);

        when(userProfileRepository.findById(userId)).thenReturn(Optional.of(userProfile));

        UserProfileResponseDTO editResult = userProfileService.editProfile(userId, newUserProfile);

        assertEquals(userId, editResult.getId());
        assertEquals("Jane", editResult.getFirstName());
        assertEquals("Doe", editResult.getLastName());
        assertEquals("jane.doe@example.com", editResult.getEmail());
    }
}
