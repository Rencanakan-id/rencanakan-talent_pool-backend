//package rencanakan.id.talentpool.controller;
//
//import static org.mockito.Mockito.*;
//import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
//import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
//
//import com.fasterxml.jackson.databind.ObjectMapper;
//import jakarta.validation.ConstraintViolation;
//import jakarta.validation.Validator;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.mockito.Mock;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
//import org.springframework.boot.test.mock.mockito.MockBean;
//import org.springframework.http.MediaType;
//import org.springframework.test.web.servlet.MockMvc;
//
//import java.util.Arrays;
//import java.util.Collections;
//import java.util.List;
//import java.util.Set;
//
//import rencanakan.id.talentpool.dto.UserProfileRequestDTO;
//import rencanakan.id.talentpool.dto.UserProfileResponseDTO;
//import rencanakan.id.talentpool.service.UserProfileService;
//import rencanakan.id.talentpool.model.User;
//
//@WebMvcTest(UserProfileController.class)
//public class UserControllerTest {
//
//    @Autowired
//    private MockMvc mockMvc;
//
//    @Mock
//    private Validator validator;
//
//    @MockBean
//    private UserProfileService userProfileService;
//
//    private UserProfileResponseDTO userProfileResponseDTO;
//
//    private final ObjectMapper objectMapper = new ObjectMapper();
//
//    @BeforeEach
//    void setUp() {
//        // Create a sample UserProfileResponseDTO for testing with correct properties
//        userProfileResponseDTO = new UserProfileResponseDTO();
//        userProfileResponseDTO.setId("user123");
//        userProfileResponseDTO.setFirstName("John");
//        userProfileResponseDTO.setLastName("Doe");
//        userProfileResponseDTO.setEmail("john.doe@example.com");
//        userProfileResponseDTO.setPhoneNumber("08123456789");
//        userProfileResponseDTO.setAddress("Jakarta, Indonesia");
//        userProfileResponseDTO.setJob("Software Developer");
//        userProfileResponseDTO.setPhoto("profile.jpg");
//        userProfileResponseDTO.setAboutMe("Experienced developer");
//        userProfileResponseDTO.setExperienceYears(5);
//        userProfileResponseDTO.setCurrentLocation("Jakarta");
//        userProfileResponseDTO.setPreferredLocations(Arrays.asList("Jakarta", "Bandung", "Surabaya"));
//        userProfileResponseDTO.setSkill("Java, Spring Boot");
//    }
//
//    private UserProfileRequestDTO createValidRequestDTO() {
//        UserProfileRequestDTO dto = new UserProfileRequestDTO();
//        dto.setFirstName("John");
//        dto.setLastName("Doe");
//        dto.setEmail("john.doe@example.com");
//        dto.setPhoneNumber("1234567890");
//        dto.setAddress("Jalan Margonda");
//        dto.setJob("Arsitek");
//        dto.setAboutMe("Hola");
//        dto.setNik("1234567890123456");
//        dto.setNpwp("123456789");
//        dto.setExperienceYears(5);
//        dto.setSkkLevel("Profesional");
//        dto.setCurrentLocation("Jakarta");
//        dto.setPreferredLocations(List.of("Bali", "Bandung"));
//        dto.setSkill("Arsitektur");
//        return dto;
//    }
//
//    private User createMockProfile() {
//        return User.builder()
//                .firstName("John")
//                .lastName("Doe")
//                .email("john.doe@example.com")
//                .phoneNumber("1234567890")
//                .address("Jalan Margonda")
//                .job("Arsitek")
//                .aboutMe("Hola")
//                .nik("1234567890123456")
//                .npwp("123456789")
//                .experienceYears(5)
//                .skkLevel("Profesional")
//                .currentLocation("Jakarta")
//                .preferredLocations(List.of("Bali", "Bandung"))
//                .skill("Arsitektur")
//                .build();
//    }
//
//    @Test
//    void testGetExperiencesByTalentId() throws Exception {
//        // Arrange
//        String userId = "user123";
//        when(userProfileService.getById(userId)).thenReturn(userProfileResponseDTO);
//
//        // Act & Assert
//        mockMvc.perform(get("/api/user-profiles/{id}", userId)
//                .header("Authorization", "Bearer test-token")
//                .contentType(MediaType.APPLICATION_JSON))
//                .andExpect(status().isOk())
//                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
//                .andExpect(jsonPath("$.data.id").value(userId))
//                .andExpect(jsonPath("$.data.firstName").value("John"))
//                .andExpect(jsonPath("$.data.lastName").value("Doe"))
//                .andExpect(jsonPath("$.data.email").value("john.doe@example.com"))
//                .andExpect(jsonPath("$.data.job").value("Software Developer"))
//                .andExpect(jsonPath("$.data.experienceYears").value(5));
//
//        // Verify that the service method was called
//        verify(userProfileService, times(1)).getById(userId);
//    }
//
//    @Test
//    void testGetExperiencesByTalentIdNotFound() throws Exception {
//        // Arrange
//        String userId = "nonexistent";
//        when(userProfileService.getById(userId)).thenReturn(null);
//
//        // Act & Assert
//        mockMvc.perform(get("/api/user-profiles/{id}", userId)
//                .header("Authorization", "Bearer test-token")
//                .contentType(MediaType.APPLICATION_JSON))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$.data").isEmpty());
//
//        // Verify that the service method was called
//        verify(userProfileService, times(1)).getById(userId);
//    }
//
//    @Test
//    void testEditProfileWithValidValue() throws Exception {
//        String userId = "user123";
//
//        User updatedProfile = new User();
//        updatedProfile.setId(userId);
//        updatedProfile.setFirstName("Jane");
//        updatedProfile.setLastName("Doe");
//        updatedProfile.setEmail("jane.doe@example.com");
//
//        updatedProfile.setPassword("password12345");
//        updatedProfile.setPhoneNumber("08123456789");
//        updatedProfile.setNik("1234567891234567");
//
//        UserProfileResponseDTO responseDTO = new UserProfileResponseDTO();
//        responseDTO.setId(userId);
//        responseDTO.setFirstName("Jane");
//        responseDTO.setLastName("Doe");
//        responseDTO.setEmail("jane.doe@example.com");
//
//        when(userProfileService.editProfile(eq(userId), any(User.class))).thenReturn(responseDTO);
//
//        mockMvc.perform(put("/api/user-profiles/edit/{id}", userId)
//                        .header("Authorization", "Bearer test-token")
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content(objectMapper.writeValueAsString(updatedProfile)))
//                .andExpect(status().isOk())
//                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
//                .andExpect(jsonPath("$.data.id").value(userId))
//                .andExpect(jsonPath("$.data.firstName").value("Jane"))
//                .andExpect(jsonPath("$.data.lastName").value("Doe"))
//                .andExpect(jsonPath("$.data.email").value("jane.doe@example.com"));
//
//        verify(userProfileService, times(1)).editProfile(eq(userId), any(User.class));
//    }
//
//    @Test
//    public void testCreateExperience_ValidRequest() throws Exception {
//        UserProfileRequestDTO request = new UserProfileRequestDTO();
//        request.setFirstName("John");
//        request.setLastName("Doe");
//        request.setEmail("john.doe@example.com");
//        request.setPhoneNumber("1234567890");
//        request.setAddress("123 Main St");
//        request.setJob("Software Engineer");
//        request.setPhoto("photo.jpg");
//        request.setAboutMe("Experienced developer");
//        request.setNik("1234567890123456");
//        request.setNpwp("123456789");
//        request.setPhotoKtp("ktp.jpg");
//        request.setPhotoNpwp("npwp.jpg");
//        request.setPhotoIjazah("ijazah.jpg");
//        request.setExperienceYears(5);
//        request.setSkkLevel("Senior");
//        request.setCurrentLocation("Jakarta");
//        request.setPreferredLocations(List.of("Jakarta", "Bandung"));
//        request.setSkill("Java, Spring Boot");
//
//        User mockProfile = new User();
//        mockProfile.setFirstName(request.getFirstName());
//        mockProfile.setLastName(request.getLastName());
//        mockProfile.setEmail(request.getEmail());
//        mockProfile.setPhoneNumber(request.getPhoneNumber());
//        mockProfile.setAddress(request.getAddress());
//        mockProfile.setJob(request.getJob());
//        mockProfile.setPhoto(request.getPhoto());
//        mockProfile.setAboutMe(request.getAboutMe());
//        mockProfile.setNik(request.getNik());
//        mockProfile.setNpwp(request.getNpwp());
//        mockProfile.setPhotoKtp(request.getPhotoKtp());
//        mockProfile.setPhotoNpwp(request.getPhotoNpwp());
//        mockProfile.setPhotoIjazah(request.getPhotoIjazah());
//        mockProfile.setExperienceYears(request.getExperienceYears());
//        mockProfile.setSkkLevel(request.getSkkLevel());
//        mockProfile.setCurrentLocation(request.getCurrentLocation());
//        mockProfile.setPreferredLocations(request.getPreferredLocations());
//        mockProfile.setSkill(request.getSkill());
//
//        when(validator.validate(any(UserProfileRequestDTO.class))).thenReturn(Collections.emptySet());
//        when(userProfileService.createProfile(any(UserProfileRequestDTO.class))).thenReturn(mockProfile);
//
//        mockMvc.perform(post("/api/user-profiles/create")
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content(objectMapper.writeValueAsString(request)))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$.firstName").value("John"))
//                .andExpect(jsonPath("$.lastName").value("Doe"))
//                .andExpect(jsonPath("$.email").value("john.doe@example.com"))
//                .andExpect(jsonPath("$.phoneNumber").value("1234567890"))
//                .andExpect(jsonPath("$.address").value("123 Main St"))
//                .andExpect(jsonPath("$.job").value("Software Engineer"))
//                .andExpect(jsonPath("$.photo").value("photo.jpg"))
//                .andExpect(jsonPath("$.aboutMe").value("Experienced developer"))
//                .andExpect(jsonPath("$.nik").value("1234567890123456"))
//                .andExpect(jsonPath("$.npwp").value("123456789"))
//                .andExpect(jsonPath("$.photoKtp").value("ktp.jpg"))
//                .andExpect(jsonPath("$.photoNpwp").value("npwp.jpg"))
//                .andExpect(jsonPath("$.photoIjazah").value("ijazah.jpg"))
//                .andExpect(jsonPath("$.experienceYears").value(5))
//                .andExpect(jsonPath("$.skkLevel").value("Senior"))
//                .andExpect(jsonPath("$.currentLocation").value("Jakarta"))
//                .andExpect(jsonPath("$.preferredLocations[0]").value("Jakarta"))
//                .andExpect(jsonPath("$.preferredLocations[1]").value("Bandung"))
//                .andExpect(jsonPath("$.skill").value("Java, Spring Boot"));
//
//        verify(userProfileService, times(1)).createProfile(any(UserProfileRequestDTO.class));
//    }
//
//    @Test
//    public void testCreateExperience_InvalidRequest_MissingFields() throws Exception {
//        UserProfileRequestDTO request = new UserProfileRequestDTO();
//        request.setFirstName(""); // Invalid: NotBlank
//        request.setLastName(""); // Invalid: NotBlank
//        request.setEmail(""); // Invalid: NotBlank
//        request.setPhoneNumber(""); // Invalid: NotBlank
//        request.setAddress(null); // Invalid: NotBlank
//        request.setJob(null); // Invalid: NotBlank
//        request.setExperienceYears(null); // Invalid: NotNull
//        request.setSkkLevel(""); // Invalid: NotBlank
//        request.setCurrentLocation(""); // Invalid: NotBlank
//        request.setPreferredLocations(null); // Invalid: NotNull
//        request.setSkill(""); // Invalid: NotBlank
//
//        ConstraintViolation<UserProfileRequestDTO> violation = mock(ConstraintViolation.class);
//        when(violation.getMessage()).thenReturn("Validation error");
//        Set<ConstraintViolation<UserProfileRequestDTO>> violations = Set.of(violation);
//
//        when(validator.validate(any(UserProfileRequestDTO.class))).thenReturn(violations);
//        when(userProfileService.createProfile(any(UserProfileRequestDTO.class)))
//                .thenThrow(new IllegalArgumentException("Validation failed"));
//
//        mockMvc.perform(post("/api/user-profiles/create")
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content(objectMapper.writeValueAsString(request)))
//                .andExpect(status().isBadRequest());
//
//        verify(userProfileService, times(1)).createProfile(any(UserProfileRequestDTO.class));
//    }
//}
