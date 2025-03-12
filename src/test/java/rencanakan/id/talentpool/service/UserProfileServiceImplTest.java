package rencanakan.id.talentpool.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import java.util.Optional;
import java.util.Arrays;
import java.util.UUID;

import rencanakan.id.talentpool.dto.UserProfileResponseDTO;
import rencanakan.id.talentpool.model.UserProfile;
import rencanakan.id.talentpool.repository.UserProfileRepository;

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
    void testGetById_Success() {
        // Arrange
        String userId = UUID.randomUUID().toString();
        UserProfile userProfile = new UserProfile();
        userProfile.setId(userId);
        userProfile.setFirstName("John");
        userProfile.setLastName("Doe");
        userProfile.setEmail("john.doe@example.com");
        userProfile.setPassword("Password123");
        userProfile.setPhoneNumber("081234567890");
        userProfile.setAddress("123 Main Street");
        userProfile.setJob("Software Engineer");
        userProfile.setPhoto("profile.jpg");
        userProfile.setAboutMe("Descriptions about me");
        userProfile.setNik("1234567890123456");
        userProfile.setNpwp("12.345.678.9-012.345");
        userProfile.setPhotoKtp("ktp.jpg");
        userProfile.setPhotoNpwp("npwp.jpg");
        userProfile.setPhotoIjazah("ijazah.jpg");
        userProfile.setExperienceYears(5);
        userProfile.setSkkLevel("Profesional");
        userProfile.setCurrentLocation("Jakarta");
        userProfile.setPreferredLocations(Arrays.asList("Jakarta", "Bandung", "Surabaya"));
        userProfile.setSkill("Sipil");

        when(userProfileRepository.findById(userId)).thenReturn(Optional.of(userProfile));

        // Act
        UserProfileResponseDTO result = userProfileService.getById(userId);

        // Assert
        assertNotNull(result);
        assertEquals(userId, result.getId());
        assertEquals("John", result.getFirstName());
        assertEquals("Doe", result.getLastName());
        assertEquals("john.doe@example.com", result.getEmail());
        assertEquals("081234567890", result.getPhoneNumber());
        assertEquals("123 Main Street", result.getAddress());
        assertEquals("Software Engineer", result.getJob());
        assertEquals("profile.jpg", result.getPhoto());
        assertEquals("Descriptions about me", result.getAboutMe());
        assertEquals("1234567890123456", result.getNik());
        assertEquals("12.345.678.9-012.345", result.getNpwp());
        assertEquals("ktp.jpg", result.getPhotoKtp());
        assertEquals("npwp.jpg", result.getPhotoNpwp());
        assertEquals("ijazah.jpg", result.getPhotoIjazah());
        assertEquals(5, result.getExperienceYears());
        assertEquals("Profesional", result.getSkkLevel());
        assertEquals("Jakarta", result.getCurrentLocation());
        assertEquals(Arrays.asList("Jakarta", "Bandung", "Surabaya"), result.getPreferredLocations());
        assertEquals("Sipil", result.getSkill());

        verify(userProfileRepository, times(1)).findById(userId);
    }

    @Test
    void testGetById_NotFound() {
        // Arrange
        String userId = "nonexistent";
        when(userProfileRepository.findById(userId)).thenReturn(Optional.empty());

        // Act
        UserProfileResponseDTO result = userProfileService.getById(userId);

        // Assert
        assertNull(result);
        verify(userProfileRepository, times(1)).findById(userId);
    }
}
