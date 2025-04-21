package rencanakan.id.talentpool.unit.controller;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.persistence.EntityNotFoundException;
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
import java.util.Collections;
import java.util.List;

import rencanakan.id.talentpool.controller.ErrorController;
import rencanakan.id.talentpool.controller.UserController;
import rencanakan.id.talentpool.dto.FilterTalentDTO;
import rencanakan.id.talentpool.dto.UserRequestDTO;
import rencanakan.id.talentpool.dto.UserResponseDTO;
import rencanakan.id.talentpool.model.User;
import rencanakan.id.talentpool.service.UserService;

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
    
    private User testUser;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setId("user123");
        testUser.setEmail("john.doe@example.com");
        
        mockMvc = MockMvcBuilders
            .standaloneSetup(userController)
                .setControllerAdvice(new ErrorController())
            .setCustomArgumentResolvers(new PrincipalDetailsArgumentResolver(testUser))
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

    private UserRequestDTO createUserRequestDTO() {
        return UserRequestDTO.builder()
                .firstName("John")
                .lastName("Doe")
                .email("john.doe@example.com")
                .password("password123")
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
        @DisplayName("Should return 404 when user not found")
        void testGetUserById_NotFound() throws Exception {
            String userId = "nonexistent";
            when(userService.getById(userId)).thenReturn(null);

            mockMvc.perform(get("/users/{id}", userId)
                    .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.errors").value("User not found."));

            verify(userService, times(1)).getById(userId);
        }

        @Test
        @DisplayName("Should return 401 when user is unauthorized for get by ID")
        void testGetUserById_Unauthorized() throws Exception {
            String userId = "user123";
            
            // Set up mock with null user (unauthorized)
            mockMvc = MockMvcBuilders
                .standaloneSetup(userController)
                .setCustomArgumentResolvers(new PrincipalDetailsArgumentResolver(null))
                .build();

            mockMvc.perform(get("/users/{id}", userId)
                    .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isUnauthorized())
                    .andExpect(jsonPath("$.errors").value("Unauthorized access."));

            verify(userService, never()).getById(any());
        }
        
        @Test
        @DisplayName("Should return current user when using /me endpoint")
        void testGetCurrentUser_Success() throws Exception {
            UserResponseDTO responseDTO = createUserResponseDTO();
            when(userService.getById(any())).thenReturn(responseDTO);

            mockMvc.perform(get("/users/me")
                    .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$.data.id").value(testUser.getId()))
                    .andExpect(jsonPath("$.data.firstName").value("John"))
                    .andExpect(jsonPath("$.data.lastName").value("Doe"))
                    .andExpect(jsonPath("$.data.email").value("john.doe@example.com"))
                    .andExpect(jsonPath("$.data.phoneNumber").value("08123456789"))
                    .andExpect(jsonPath("$.data.photo").value("profile.jpg"))
                    .andExpect(jsonPath("$.data.aboutMe").value("Experienced developer"))
                    .andExpect(jsonPath("$.data.nik").value("1234567890123456"))
                    .andExpect(jsonPath("$.data.npwp").value("123456789"))
                    .andExpect(jsonPath("$.data.photoKtp").value("ktp.jpg"))
                    .andExpect(jsonPath("$.data.photoNpwp").value("npwp.jpg"))
                    .andExpect(jsonPath("$.data.photoIjazah").value("ijazah.jpg"))
                    .andExpect(jsonPath("$.data.experienceYears").value(5))
                    .andExpect(jsonPath("$.data.skkLevel").value("Senior"))
                    .andExpect(jsonPath("$.data.currentLocation").value("Jakarta"))
                    .andExpect(jsonPath("$.data.preferredLocations[0]").value("Jakarta"))
                    .andExpect(jsonPath("$.data.preferredLocations[1]").value("Bandung"))
                    .andExpect(jsonPath("$.data.preferredLocations[2]").value("Surabaya"))
                    .andExpect(jsonPath("$.data.skill").value("Java, Spring Boot"))
                    .andExpect(jsonPath("$.data.price").value(5000000));

            verify(userService, times(1)).getById(any());
        }
        
        @Test
        @DisplayName("Should return 404 when user is not found")
        void testGetCurrentUser_NotFound() throws Exception {
            when(userService.getById(any())).thenReturn(null);
            
            mockMvc.perform(get("/users/me")
                    .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.errors").value("User not found."));
                    
            verify(userService, times(1)).getById(any());
        }
    }

    @Nested
    @DisplayName("Update User Tests")
    class UpdateUserTests {
        
        @Test
        @DisplayName("Should update user with valid data")
        void testUpdateUser_Success() throws Exception {
            String userId = "user123";
            UserRequestDTO updatedUser = createUserRequestDTO();
            updatedUser.setFirstName("Jane");
            updatedUser.setLastName("Smith");
            
            UserResponseDTO responseDTO = createUserResponseDTO();
            responseDTO.setFirstName("Jane");
            responseDTO.setLastName("Smith");
            
            when(userService.editById(eq(userId), any(UserRequestDTO.class))).thenReturn(responseDTO);

            mockMvc.perform(put("/users/{id}", userId)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(updatedUser)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.data.id").value(userId))
                    .andExpect(jsonPath("$.data.firstName").value("Jane"))
                    .andExpect(jsonPath("$.data.lastName").value("Smith"));

            verify(userService, times(1)).editById(eq(userId), any(UserRequestDTO.class));
        }

        @Test
        @DisplayName("Should return forbidden when updating non-existent user")
        void testUpdateUser_UserNotFound() throws Exception {
            String userId = "nonexistent";
            UserRequestDTO updatedUser = createUserRequestDTO();
            
            mockMvc.perform(put("/users/{id}", userId)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(updatedUser)))
                    .andExpect(status().isForbidden())
                    .andExpect(jsonPath("$.errors").value("You are not authorized to edit this user."));
            
            verify(userService, never()).editById(eq(userId), any(UserRequestDTO.class));
        }
        
        @Test
        @DisplayName("Should return forbidden when updating another user's profile")
        void testUpdateUser_Forbidden() throws Exception {
            String otherUserId = "other123";
            UserRequestDTO updatedUser = createUserRequestDTO();
            
            mockMvc.perform(put("/users/{id}", otherUserId)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(updatedUser)))
                    .andExpect(status().isForbidden())
                    .andExpect(jsonPath("$.errors").value("You are not authorized to edit this user."));
            
            verify(userService, never()).editById(eq(otherUserId), any(UserRequestDTO.class));
        }
    }
    @Nested
    class FilterTalent{
        @Test
        void getAllTalent_withNameFilter_returnsFilteredUsers() throws Exception {

            UserResponseDTO userDto = UserResponseDTO.builder().firstName("John").lastName("Doe").build();

            when(userService.filter(any(FilterTalentDTO.class))).thenReturn(List.of(userDto));

            mockMvc.perform(get("/users/contractor")
                            .param("name", "john"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.data[0].firstName").value("John"))
                    .andExpect(jsonPath("$.data[0].lastName").value("Doe"));
        }

        @Test
        void getAllTalent_withNonMatchingName_throwsEntityNotFoundException() throws Exception {
              when(userService.filter(any(FilterTalentDTO.class)))
                    .thenThrow(new EntityNotFoundException("No users found"));
            mockMvc.perform(get("/users/contractor")
                            .param("name", "nonexistent"))
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.errors").value("No users found"));
        }

        @Test
        void getAllTalent_withNullName_returnsAllUsers() throws Exception {

            UserResponseDTO user1 =UserResponseDTO.builder().firstName("Alice").build();
            UserResponseDTO user2 = UserResponseDTO.builder().firstName("Bob").build();

            List<UserResponseDTO> allUsers = List.of(user1, user2);

            when(userService.filter(any(FilterTalentDTO.class))).thenReturn(allUsers);

            mockMvc.perform(get("/users/contractor"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.data[0].firstName").value("Alice"))
                    .andExpect(jsonPath("$.data[1].firstName").value("Bob"));
        }
    }
}
