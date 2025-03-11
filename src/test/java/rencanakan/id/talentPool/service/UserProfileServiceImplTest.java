package rencanakan.id.talentPool.service;

import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.lang.reflect.Field;
import java.util.Set;

import rencanakan.id.talentPool.dto.UserProfileRequestDTO;
import rencanakan.id.talentPool.dto.UserProfileResponseDTO;
import rencanakan.id.talentPool.model.UserProfile;
import rencanakan.id.talentPool.repository.UserProfileRepository;

public class UserProfileServiceImplTest {

    @Mock
    private UserProfileRepository userProfileRepository;

    @Mock
    private Validator mockValidator;

    @InjectMocks
    private UserProfileServiceImpl userProfileService;

    @Captor
    private ArgumentCaptor<UserProfile> userProfileCaptor;

    private static Validator validator;

    private UserProfileRequestDTO createValidRequestDTO() {
        UserProfileRequestDTO dto = new UserProfileRequestDTO();
        dto.setFirstName("John");
        dto.setLastName("Doe");
        dto.setEmail("john.doe@example.com");
        dto.setPhoneNumber("1234567890");
        dto.setAddress("Jalan Margonda");
        dto.setJob("Arsitek");
        dto.setAboutMe("Hola");
        dto.setNik("1234567890123456");
        dto.setNpwp("123456789");
        dto.setExperienceYears(5);
        dto.setSkkLevel("Profesional");
        dto.setCurrentLocation("Jakarta");
        dto.setPreferredLocations(List.of("Bali", "Bandung"));
        dto.setSkill("Arsitektur");
        return dto;
    }

    private UserProfile createMockProfile() {
        return UserProfile.builder()
                .firstName("John")
                .lastName("Doe")
                .email("john.doe@example.com")
                .phoneNumber("1234567890")
                .address("Jalan Margonda")
                .job("Arsitek")
                .aboutMe("Hola")
                .nik("1234567890123456")
                .npwp("123456789")
                .experienceYears(5)
                .skkLevel("Profesional")
                .currentLocation("Jakarta")
                .preferredLocations(List.of("Bali", "Bandung"))
                .skill("Arsitektur")
                .build();
    }

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @BeforeAll
    public static void setupValidator() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    public void createProfileValid() {
        UserProfileRequestDTO request = createValidRequestDTO();
        UserProfile expectedProfile = createMockProfile();

        when(userProfileRepository.save(any(UserProfile.class))).thenReturn(expectedProfile);

        UserProfile createdProfile = userProfileService.createProfile(request);

        verify(userProfileRepository).save(userProfileCaptor.capture());
        UserProfile capturedProfile = userProfileCaptor.getValue();

        assertNotNull(createdProfile);

        assertEquals(request.getFirstName(), capturedProfile.getFirstName());
        assertEquals(request.getLastName(), capturedProfile.getLastName());
        assertEquals(request.getEmail(), capturedProfile.getEmail());
        assertEquals(request.getPhoneNumber(), capturedProfile.getPhoneNumber());
        assertEquals(request.getAddress(), capturedProfile.getAddress());
        assertEquals(request.getJob(), capturedProfile.getJob());
        assertEquals(request.getAboutMe(), capturedProfile.getAboutMe());
        assertEquals(request.getNik(), capturedProfile.getNik());
        assertEquals(request.getNpwp(), capturedProfile.getNpwp());
        assertEquals(request.getExperienceYears(), capturedProfile.getExperienceYears());
        assertEquals(request.getSkkLevel(), capturedProfile.getSkkLevel());
        assertEquals(request.getCurrentLocation(), capturedProfile.getCurrentLocation());
        assertEquals(request.getPreferredLocations(), capturedProfile.getPreferredLocations());
        assertEquals(request.getSkill(), capturedProfile.getSkill());

        assertEquals(expectedProfile, createdProfile);

    }

    @Test
    void testCreateProfile_ValidationFailure() {
        UserProfileRequestDTO request = new UserProfileRequestDTO();
        when(mockValidator.validate(request)).thenReturn(validator.validate(request));
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            userProfileService.createProfile(request);
        });
        assertEquals("Validation failed", exception.getMessage());
        verify(userProfileRepository, never()).save(any(UserProfile.class));
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
        List<String> preferredLocations = new ArrayList<>();
        preferredLocations.add("Bekasi");
        preferredLocations.add("Depok");
        preferredLocations.add("Bogor");

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
        newUserProfile.setPreferredLocations(preferredLocations);
        newUserProfile.setJob("Software Engineering");
        newUserProfile.setSkkLevel("Professional");
        newUserProfile.setPhoto("photo.jpg");
        newUserProfile.setNik("1234567891011121");
        newUserProfile.setNpwp("01122334456789101231");
        newUserProfile.setPhotoKtp("ktp.jpg");
        newUserProfile.setPhotoNpwp("npwp.jpg");
        newUserProfile.setPhotoIjazah("ijazah.jpg");
        newUserProfile.setSkill("Cooking, Mewing");

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
    public void testEdit_InvalidEmail() throws Exception {
        String userId = "user123";
        UserProfile userProfile = new UserProfile();
        userProfile.setId(userId);
        userProfile.setFirstName("John");
        userProfile.setLastName("Doe");
        userProfile.setEmail("john.doe@example.com");

        when(userProfileRepository.findById(userId)).thenReturn(Optional.of(userProfile));

        UserProfile invalidUserProfile = new UserProfile();

        Field emailField = UserProfile.class.getDeclaredField("email");
        emailField.setAccessible(true);
        emailField.set(invalidUserProfile, "invalid-email");

        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            userProfileService.editProfile(userId, invalidUserProfile);
        });

        assertEquals("Invalid email format", exception.getMessage());
    }

    @Test
    public void testEdit_InvalidPassword() throws Exception {
        String userId = "user123";
        UserProfile userProfile = new UserProfile();
        userProfile.setId(userId);
        userProfile.setFirstName("John");
        userProfile.setLastName("Doe");
        userProfile.setPassword("password123");

        when(userProfileRepository.findById(userId)).thenReturn(Optional.of(userProfile));

        UserProfile invalidUserProfile = new UserProfile();

        Field passwordField = UserProfile.class.getDeclaredField("password");
        passwordField.setAccessible(true);
        passwordField.set(invalidUserProfile, "pass");

        invalidUserProfile.setId(userId);

        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            userProfileService.editProfile(userId, invalidUserProfile);
        });

        assertEquals("Password must be at least 8 characters", exception.getMessage());
    }

    @Test
    public void testEdit_InvalidNIK() throws Exception {
        String userId = "user123";
        UserProfile userProfile = new UserProfile();
        userProfile.setId(userId);
        userProfile.setFirstName("John");
        userProfile.setLastName("Doe");
        userProfile.setNik("1234567891011121");

        when(userProfileRepository.findById(userId)).thenReturn(Optional.of(userProfile));

        UserProfile invalidUserProfile = new UserProfile();

        Field nikField = UserProfile.class.getDeclaredField("nik");
        nikField.setAccessible(true);
        nikField.set(invalidUserProfile, "invalid-nik");

        invalidUserProfile.setId(userId);

        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            userProfileService.editProfile(userId, invalidUserProfile);
        });

        assertEquals("NIK must be exactly 16 digits", exception.getMessage());
    }

    @Test
    public void testEdit_UserNotFound() throws Exception {
        String userId = "user123";
        UserProfile userProfile = new UserProfile();
        userProfile.setId(userId);
        userProfile.setFirstName("John");
        userProfile.setLastName("Doe");

        when(userProfileRepository.findById(userId)).thenReturn(Optional.of(userProfile));

        UserProfileResponseDTO editResult = userProfileService.editProfile("invalid-id", new UserProfile());

        assertNull(editResult);
    }

    @Test
    public void testEdit_CharTooLong() throws Exception {
        String userId = "user123";
        UserProfile userProfile = new UserProfile();
        userProfile.setId(userId);
        userProfile.setFirstName("John");
        userProfile.setLastName("Doe");
        userProfile.setEmail("john.doe@example.com");

        when(userProfileRepository.findById(userId)).thenReturn(Optional.of(userProfile));

        UserProfile invalidUserProfile = new UserProfile();
        invalidUserProfile.setId(userId);

        String longName = "A".repeat(300);
        Field nameField = UserProfile.class.getDeclaredField("firstName");
        nameField.setAccessible(true);
        nameField.set(invalidUserProfile, longName);

        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            userProfileService.editProfile(userId, invalidUserProfile);
        });

        assertEquals("First name exceeds maximum length", exception.getMessage());
    }




}
