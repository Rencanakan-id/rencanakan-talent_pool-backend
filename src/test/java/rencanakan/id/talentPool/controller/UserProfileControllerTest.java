package rencanakan.id.talentpool.controller;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import rencanakan.id.talentpool.dto.UserProfileResponseDTO;
import rencanakan.id.talentpool.service.UserProfileService;

@WebMvcTest(UserProfileController.class)
public class UserProfileControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserProfileService userProfileService;

    private UserProfileResponseDTO userProfileResponseDTO;

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
    void testGetUserProfileById() throws Exception {
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
    void testGetUserProfileByIdNotFound() throws Exception {
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
}
