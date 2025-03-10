package rencanakan.id.talentPool.controller;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import rencanakan.id.talentPool.dto.UserProfileResponseDTO;
import rencanakan.id.talentPool.service.UserProfileService;
import rencanakan.id.talentPool.model.UserProfile;

@WebMvcTest(UserProfileController.class)
public class UserProfileControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserProfileService userProfileService;

    private UserProfileResponseDTO userProfileResponseDTO;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void setUp() {
        // Create a sample UserProfileResponseDTO for testing with correct properties
        userProfileResponseDTO = new UserProfileResponseDTO();
        userProfileResponseDTO.setId("user123");
        userProfileResponseDTO.setFirstName("John");
        userProfileResponseDTO.setLastName("Doe");
        userProfileResponseDTO.setEmail("john.doe@example.com");
        userProfileResponseDTO.setPhoneNumber("08123456789");
        userProfileResponseDTO.setAddress("Jakarta, Indonesia");
        userProfileResponseDTO.setJob("Software Developer");
        userProfileResponseDTO.setPhoto("profile.jpg");
        userProfileResponseDTO.setAboutMe("Experienced developer");
        userProfileResponseDTO.setExperienceYears(5);
        userProfileResponseDTO.setCurrentLocation("Jakarta");
        userProfileResponseDTO.setPreferredLocations(Arrays.asList("Jakarta", "Bandung", "Surabaya"));
        userProfileResponseDTO.setSkill("Java, Spring Boot");
    }

    @Test
    void testGetExperiencesByTalentId() throws Exception {
        // Arrange
        String userId = "user123";
        when(userProfileService.getById(userId)).thenReturn(userProfileResponseDTO);

        // Act & Assert
        mockMvc.perform(get("/api/user-profiles/{id}", userId)
                .header("Authorization", "Bearer test-token")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.data.id").value(userId))
                .andExpect(jsonPath("$.data.firstName").value("John"))
                .andExpect(jsonPath("$.data.lastName").value("Doe"))
                .andExpect(jsonPath("$.data.email").value("john.doe@example.com"))
                .andExpect(jsonPath("$.data.job").value("Software Developer"))
                .andExpect(jsonPath("$.data.experienceYears").value(5));

        // Verify that the service method was called
        verify(userProfileService, times(1)).getById(userId);
    }

    @Test
    void testGetExperiencesByTalentIdNotFound() throws Exception {
        // Arrange
        String userId = "nonexistent";
        when(userProfileService.getById(userId)).thenReturn(null);

        // Act & Assert
        mockMvc.perform(get("/api/user-profiles/{id}", userId)
                .header("Authorization", "Bearer test-token")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").isEmpty());

        // Verify that the service method was called
        verify(userProfileService, times(1)).getById(userId);
    }

    @Test
    void testEditProfileWithValidValue() throws Exception {
        String userId = "user123";

        UserProfile updatedProfile = new UserProfile();
        updatedProfile.setId(userId);
        updatedProfile.setFirstName("Jane");
        updatedProfile.setLastName("Doe");
        updatedProfile.setEmail("jane.doe@example.com");

        updatedProfile.setPassword("password12345");
        updatedProfile.setPhoneNumber("08123456789");
        updatedProfile.setNik("1234567891234567");

        UserProfileResponseDTO responseDTO = new UserProfileResponseDTO();
        responseDTO.setId(userId);
        responseDTO.setFirstName("Jane");
        responseDTO.setLastName("Doe");
        responseDTO.setEmail("jane.doe@example.com");

        when(userProfileService.editProfile(eq(userId), any(UserProfile.class))).thenReturn(responseDTO);

        mockMvc.perform(put("/api/user-profiles/edit/{id}", userId)
                        .header("Authorization", "Bearer test-token")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updatedProfile)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.data.id").value(userId))
                .andExpect(jsonPath("$.data.firstName").value("Jane"))
                .andExpect(jsonPath("$.data.lastName").value("Doe"))
                .andExpect(jsonPath("$.data.email").value("jane.doe@example.com"));

        verify(userProfileService, times(1)).editProfile(eq(userId), any(UserProfile.class));
    }
}
