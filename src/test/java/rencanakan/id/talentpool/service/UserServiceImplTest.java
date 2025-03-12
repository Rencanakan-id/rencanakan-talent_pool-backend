package rencanakan.id.talentpool.service;

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

import rencanakan.id.talentpool.dto.UserProfileRequestDTO;
import rencanakan.id.talentpool.dto.UserProfileResponseDTO;
import rencanakan.id.talentpool.model.User;
import rencanakan.id.talentpool.repository.UserProfileRepository;

public class UserServiceImplTest {

    @Mock
    private UserProfileRepository userProfileRepository;

    @Mock
    private Validator mockValidator;

    @InjectMocks
    private UserProfileServiceImpl userProfileService;

    @Captor
    private ArgumentCaptor<User> userProfileCaptor;

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

    private User createMockProfile() {
        return User.builder()
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
        User expectedProfile = createMockProfile();

        when(userProfileRepository.save(any(User.class))).thenReturn(expectedProfile);

        User createdProfile = userProfileService.createProfile(request);

        verify(userProfileRepository).save(userProfileCaptor.capture());
        User capturedProfile = userProfileCaptor.getValue();

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
        verify(userProfileRepository, never()).save(any(User.class));
    }

    @Test
    public void testGetById_Success() {
        // Arrange
        String userId = "user123";
        User user = new User();
        user.setId(userId);
        user.setFirstName("John");
        user.setLastName("Doe");
        user.setEmail("john.doe@example.com");

        when(userProfileRepository.findById(userId)).thenReturn(Optional.of(user));

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
        User user = new User();
        user.setId(userId);
        user.setFirstName("John");
        user.setLastName("Cena");
        user.setEmail("john.cena12@example.com");
        user.setPassword("password123");
        user.setNik("1234567891011121");

        User newUser = new User();
        newUser.setId(userId);
        newUser.setFirstName("Jane");
        newUser.setLastName("Doe");
        newUser.setEmail("jane.doe@example.com");
        newUser.setPhoneNumber("1234567890");
        newUser.setPassword("password1234");
        newUser.setAboutMe("This is me!");
        newUser.setAddress("Jakarta Street");
        newUser.setCurrentLocation("New York");
        newUser.setExperienceYears(6);
        newUser.setPreferredLocations(preferredLocations);
        newUser.setJob("Software Engineering");
        newUser.setSkkLevel("Professional");
        newUser.setPhoto("photo.jpg");
        newUser.setNik("1234567891011121");
        newUser.setNpwp("01122334456789101231");
        newUser.setPhotoKtp("ktp.jpg");
        newUser.setPhotoNpwp("npwp.jpg");
        newUser.setPhotoIjazah("ijazah.jpg");
        newUser.setSkill("Cooking, Mewing");

        when(userProfileRepository.findById(userId)).thenReturn(Optional.of(user));

        UserProfileResponseDTO editResult = userProfileService.editProfile(userId, newUser);

        assertEquals(userId, editResult.getId());
        assertEquals("Jane", editResult.getFirstName());
        assertEquals("Doe", editResult.getLastName());
        assertEquals("jane.doe@example.com", editResult.getEmail());
    }

    @Test
    public void testEdit_NoChanges() {
        String userId = "user123";
        User user = new User();
        user.setId(userId);
        user.setFirstName("John");
        user.setLastName("Doe");
        user.setEmail("john.doe@example.com");

        when(userProfileRepository.findById(userId)).thenReturn(Optional.of(user));

        UserProfileResponseDTO editResult = userProfileService.editProfile(userId, new User());

        assertEquals(userId, editResult.getId());
        assertEquals("John", editResult.getFirstName());
        assertEquals("Doe", editResult.getLastName());
        assertEquals("john.doe@example.com", editResult.getEmail());
    }

    @Test
    public void testEdit_InvalidEmail() throws Exception {
        String userId = "user123";
        User user = new User();
        user.setId(userId);
        user.setFirstName("John");
        user.setLastName("Doe");
        user.setEmail("john.doe@example.com");

        when(userProfileRepository.findById(userId)).thenReturn(Optional.of(user));

        User invalidUser = new User();

        Field emailField = User.class.getDeclaredField("email");
        emailField.setAccessible(true);
        emailField.set(invalidUser, "invalid-email");

        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            userProfileService.editProfile(userId, invalidUser);
        });

        assertEquals("Invalid email format", exception.getMessage());
    }

    @Test
    public void testEdit_InvalidPassword() throws Exception {
        String userId = "user123";
        User user = new User();
        user.setId(userId);
        user.setFirstName("John");
        user.setLastName("Doe");
        user.setPassword("password123");

        when(userProfileRepository.findById(userId)).thenReturn(Optional.of(user));

        User invalidUser = new User();

        Field passwordField = User.class.getDeclaredField("password");
        passwordField.setAccessible(true);
        passwordField.set(invalidUser, "pass");

        invalidUser.setId(userId);

        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            userProfileService.editProfile(userId, invalidUser);
        });

        assertEquals("Password must be at least 8 characters", exception.getMessage());
    }

    @Test
    public void testEdit_InvalidNIK() throws Exception {
        String userId = "user123";
        User user = new User();
        user.setId(userId);
        user.setFirstName("John");
        user.setLastName("Doe");
        user.setNik("1234567891011121");

        when(userProfileRepository.findById(userId)).thenReturn(Optional.of(user));

        User invalidUser = new User();

        Field nikField = User.class.getDeclaredField("nik");
        nikField.setAccessible(true);
        nikField.set(invalidUser, "invalid-nik");

        invalidUser.setId(userId);

        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            userProfileService.editProfile(userId, invalidUser);
        });

        assertEquals("NIK must be exactly 16 digits", exception.getMessage());
    }

    @Test
    public void testEdit_UserNotFound() throws Exception {
        String userId = "user123";
        User user = new User();
        user.setId(userId);
        user.setFirstName("John");
        user.setLastName("Doe");

        when(userProfileRepository.findById(userId)).thenReturn(Optional.of(user));

        UserProfileResponseDTO editResult = userProfileService.editProfile("invalid-id", new User());

        assertNull(editResult);
    }

    @Test
    public void testEdit_CharTooLong() throws Exception {
        String userId = "user123";
        User user = new User();
        user.setId(userId);
        user.setFirstName("John");
        user.setLastName("Doe");
        user.setEmail("john.doe@example.com");

        when(userProfileRepository.findById(userId)).thenReturn(Optional.of(user));

        User invalidUser = new User();
        invalidUser.setId(userId);

        String longName = "A".repeat(300);
        Field nameField = User.class.getDeclaredField("firstName");
        nameField.setAccessible(true);
        nameField.set(invalidUser, longName);

        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            userProfileService.editProfile(userId, invalidUser);
        });

        assertEquals("First name exceeds maximum length", exception.getMessage());
    }




}
