package rencanakan.id.talentpool.unit.controller;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.validation.Validator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Arrays;
import java.util.List;

import rencanakan.id.talentpool.controller.UserController;
import rencanakan.id.talentpool.dto.UserResponseDTO;
import rencanakan.id.talentpool.service.UserService;
import rencanakan.id.talentpool.model.User;

@ExtendWith(MockitoExtension.class)
class UserControllerTest {

    @Mock
    private UserService userService;
    
    @Mock
    private Validator validator;

    @InjectMocks
    private UserController userController;
    
    private MockMvc mockMvc;
    
    private ObjectMapper objectMapper;


    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders
            .standaloneSetup(userController)
            .build();
        objectMapper = new ObjectMapper();
    }

    private UserResponseDTO createUserResponseDTO() {
        UserResponseDTO dto = new UserResponseDTO();
        dto.setId("user123");
        dto.setFirstName("John");
        dto.setLastName("Doe");
        dto.setEmail("john.doe@example.com");
        dto.setPhoneNumber("08123456789");
        dto.setPhoto("profile.jpg");
        dto.setAboutMe("Experienced developer");
        dto.setNik("1234567890123456");
        dto.setNpwp("123456789");
        dto.setPhotoKtp("ktp.jpg");
        dto.setPhotoNpwp("npwp.jpg");
        dto.setPhotoIjazah("ijazah.jpg");
        dto.setExperienceYears(5);
        dto.setSkkLevel("Senior");
        dto.setCurrentLocation("Jakarta");
        dto.setPreferredLocations(Arrays.asList("Jakarta", "Bandung", "Surabaya"));
        dto.setSkill("Java, Spring Boot");
        dto.setPrice(5000000);
        return dto;
    }

    private User createUser() {
        return User.builder()
                .id("user123")
                .firstName("John")
                .lastName("Doe")
                .email("john.doe@example.com")
                .password("hashedPassword")
                .phoneNumber("1234567890")
                .photo("profile.jpg")
                .aboutMe("Experienced developer")
                .nik("1234567890123456")
                .npwp("123456789")
                .photoKtp("ktp.jpg")
                .photoNpwp("npwp.jpg")
                .photoIjazah("ijazah.jpg")
                .experienceYears(5)
                .skkLevel("Senior")
                .currentLocation("Jakarta")
                .preferredLocations(List.of("Jakarta", "Bandung"))
                .skill("Java, Spring Boot")
                .price(5000000)
                .build();
    }

    @Nested
    @DisplayName("Get User Tests")
    class GetUserTests {
        
        @Test
        @DisplayName("Should return user when user exists")
        void testGetUserById_Success() throws Exception {
            String userId = "user123";
            UserResponseDTO responseDTO = createUserResponseDTO();
            when(userService.getById(userId)).thenReturn(responseDTO);

            mockMvc.perform(get("/users/{id}", userId)
                    .header("Authorization", "Bearer test-token")
                    .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$.data.id").value(userId))
                    .andExpect(jsonPath("$.data.firstName").value("John"))
                    .andExpect(jsonPath("$.data.lastName").value("Doe"))
                    .andExpect(jsonPath("$.data.email").value("john.doe@example.com"))
                    .andExpect(jsonPath("$.data.phoneNumber").value("08123456789"))
                    .andExpect(jsonPath("$.data.photo").value("profile.jpg"))
                    .andExpect(jsonPath("$.data.aboutMe").value("Experienced developer"))
                    .andExpect(jsonPath("$.data.nik").value("1234567890123456"))
                    .andExpect(jsonPath("$.data.npwp").value("123456789"))
                    .andExpect(jsonPath("$.data.experienceYears").value(5))
                    .andExpect(jsonPath("$.data.skkLevel").value("Senior"))
                    .andExpect(jsonPath("$.data.price").value(5000000));

            verify(userService, times(1)).getById(userId);
        }

        @Test
        @DisplayName("Should return empty data when user not found")
        void testGetUserById_NotFound() throws Exception {
            String userId = "nonexistent";
            when(userService.getById(userId)).thenReturn(null);

            mockMvc.perform(get("/users/{id}", userId)
                    .header("Authorization", "Bearer test-token")
                    .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.data").isEmpty());

            verify(userService, times(1)).getById(userId);
        }
    }

    @Nested
    @DisplayName("Update User Tests")
    class UpdateUserTests {
        
        @Test
        @DisplayName("Should update user with valid data")
        void testUpdateUser_Success() throws Exception {
            String userId = "user123";
            User updatedUser = createUser();
            updatedUser.setFirstName("Jane");
            updatedUser.setLastName("Smith");
            
            UserResponseDTO responseDTO = createUserResponseDTO();
            responseDTO.setFirstName("Jane");
            responseDTO.setLastName("Smith");
            
            when(userService.editUser(eq(userId), any(User.class))).thenReturn(responseDTO);

            mockMvc.perform(put("/users/{id}", userId)
                    .header("Authorization", "Bearer test-token")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(updatedUser)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.data.id").value(userId))
                    .andExpect(jsonPath("$.data.firstName").value("Jane"))
                    .andExpect(jsonPath("$.data.lastName").value("Smith"));

            verify(userService, times(1)).editUser(eq(userId), any(User.class));
        }

        @Test
        @DisplayName("Should return not found when updating non-existent user")
        void testUpdateUser_UserNotFound() throws Exception {
            String userId = "nonexistent";
            User updatedUser = createUser();
            
            when(userService.editUser(eq(userId), any(User.class)))
                    .thenReturn(null);

            mockMvc.perform(put("/users/{id}", userId)
                    .header("Authorization", "Bearer test-token")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(updatedUser)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.data").isEmpty());
        }
    }
}
