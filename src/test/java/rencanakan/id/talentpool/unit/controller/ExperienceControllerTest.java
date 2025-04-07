package rencanakan.id.talentpool.unit.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.servlet.mvc.method.annotation.PrincipalMethodArgumentResolver;
import rencanakan.id.talentpool.controller.ErrorController;
import rencanakan.id.talentpool.controller.ExperienceController;
import rencanakan.id.talentpool.dto.ExperienceRequestDTO;
import rencanakan.id.talentpool.dto.ExperienceResponseDTO;
import rencanakan.id.talentpool.dto.WebResponse;
import rencanakan.id.talentpool.enums.EmploymentType;
import rencanakan.id.talentpool.enums.LocationType;
import rencanakan.id.talentpool.model.User;
import rencanakan.id.talentpool.service.ExperienceServiceImpl;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
public class ExperienceControllerTest {

    @Mock
    private ExperienceServiceImpl experienceService;

    @InjectMocks
    private ExperienceController experienceController;

    private MockMvc mockMvc;

    private ObjectMapper objectMapper;

    private User user;
    private ExperienceResponseDTO response;
    private  ExperienceRequestDTO request;


    @BeforeEach
    void setUp() {
        objectMapper = Jackson2ObjectMapperBuilder.json()
                .modules(new JavaTimeModule())
                .featuresToDisable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
                .build();

        mockMvc = MockMvcBuilders.standaloneSetup(experienceController)
                .setMessageConverters(new MappingJackson2HttpMessageConverter(objectMapper))
                .setControllerAdvice(new ErrorController()) //
                .setCustomArgumentResolvers(new PrincipalMethodArgumentResolver())
                .build();


        user = createUser();
        request = createValidRequestDTO();
        response= createMockResponseDTO();;
    }

    private User createUser() {
        return User.builder()
                .id("1")
                .firstName("John")
                .lastName("Doe")
                .email("john.doe@example.com")
                .build();
    }

    private ExperienceRequestDTO createValidRequestDTO() {
        return ExperienceRequestDTO.builder()
                .title("Lead Construction Project Manager")
                .company("Aman")
                .companyImage("https://eternalsunshine.png")
                .employmentType(EmploymentType.FULL_TIME)
                .startDate(LocalDate.now())
                .endDate(LocalDate.now().plusDays(1))
                .location("Depok")
                .locationType(LocationType.ON_SITE)
                .build();
    }

    private ExperienceResponseDTO createMockResponseDTO() {
        return ExperienceResponseDTO.builder()
                .id(1L)
                .title("Lead Construction Project Manager")
                .company("Aman")
                .employmentType(EmploymentType.FULL_TIME)
                .startDate(LocalDate.now())
                .endDate(LocalDate.now().plusDays(1))
                .location("Depok")
                .locationType(LocationType.ON_SITE)
                .build();
    }
    @Nested
    class EditExperienceTest {

        @Test
        void testEditExperience_Success() throws Exception {
            Long experienceId = 1L;

            String expectedStartDate = response.getStartDate().format(DateTimeFormatter.ISO_LOCAL_DATE);
            String expectedEndDate = response.getEndDate().format(DateTimeFormatter.ISO_LOCAL_DATE);

            when(experienceService.editById(any(), eq(experienceId), any(ExperienceRequestDTO.class)))
                    .thenReturn(response);

            mockMvc.perform(put("/experiences/" + experienceId)
                            .header("Authorization", "Bearer sample-token")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.data.id").value(1L))
                    .andExpect(jsonPath("$.data.title").value("Lead Construction Project Manager"))
                    .andExpect(jsonPath("$.data.company").value("Aman"))
                    .andExpect(jsonPath("$.data.employmentType").value("FULL_TIME"))
                    .andExpect(jsonPath("$.data.startDate").value(expectedStartDate))
                    .andExpect(jsonPath("$.data.endDate").value(expectedEndDate))
                    .andExpect(jsonPath("$.data.location").value("Depok"))
                    .andExpect(jsonPath("$.data.locationType").value("ON_SITE"));
        }
        @Test
        void testValidationError() throws Exception {
            String token = "Bearer sample-token";
            Long id = 1L;
            request.setTitle(null);

            mockMvc.perform(put("/experiences/" + id)
                            .header("Authorization", token)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.errors").exists());
        }
        @Test
        public void testEditExperience_UserNotAuthorized_ReturnsForbidden() throws Exception {

            Long experienceId = 1L;

            doThrow(new AccessDeniedException("You are not allowed to edit this experience."))
                    .when(experienceService).editById(any(), eq(experienceId), any(ExperienceRequestDTO.class));

            mockMvc.perform(put("/experiences/{id}", experienceId)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isForbidden()) // Expect 403 Forbidden status
                    .andExpect(jsonPath("$.errors").value("You are not allowed to edit this experience.")); // Check error message
        }



        @Test
        void testEntityNotFoundException() throws Exception {

            Long invalidId = 999L;
            String token = "Bearer sample-token";

            when(experienceService.editById(any(), eq(invalidId), any(ExperienceRequestDTO.class)))
                    .thenThrow(new EntityNotFoundException("Experience Not Found"));

            mockMvc.perform(put("/experiences/" + invalidId)
                            .header("Authorization", token)  // Tambahkan header Authorization
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.errors").value("Experience Not Found"));  // Pastikan pesan sesuai dengan exception
            verify(experienceService, times(1)).editById(any(String.class), eq(invalidId), any(ExperienceRequestDTO.class));

        }



    }

    @Nested
    class CreateExperienceTest {
        @Test
        void testCreateExperience_Success() throws Exception {
            ExperienceRequestDTO request = createValidRequestDTO();
            ExperienceResponseDTO mockResponseDTO = createMockResponseDTO();

            String expectedStartDate = mockResponseDTO.getStartDate().format(DateTimeFormatter.ISO_LOCAL_DATE);
            String expectedEndDate = mockResponseDTO.getEndDate().format(DateTimeFormatter.ISO_LOCAL_DATE);

            when(experienceService.createExperience(any(), any(ExperienceRequestDTO.class)))
                    .thenReturn(mockResponseDTO);

            mockMvc.perform(post("/experiences")
                            .contentType(MediaType.APPLICATION_JSON)
                            .with(SecurityMockMvcRequestPostProcessors.user(user))
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.data.title").value("Lead Construction Project Manager"))
                    .andExpect(jsonPath("$.data.company").value("Aman"))
                    .andExpect(jsonPath("$.data.employmentType").value("FULL_TIME"))
                    .andExpect(jsonPath("$.data.startDate").value(expectedStartDate))
                    .andExpect(jsonPath("$.data.endDate").value(expectedEndDate))
                    .andExpect(jsonPath("$.data.location").value("Depok"))
                    .andExpect(jsonPath("$.data.locationType").value("ON_SITE"));
        }

        @Test
        void testCreateExperience_BadRequest() throws Exception {
            ExperienceRequestDTO invalidRequest = new ExperienceRequestDTO();

            mockMvc.perform(post("/experiences")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(invalidRequest)))
                    .andExpect(status().isBadRequest());
        }
    }

    @Nested
    class DeleteExperienceTest {

        @Test
        void testDeleteExperience_Success() throws Exception {
            Long experienceId = 1L;

            doNothing().when(experienceService).deleteById(experienceId);

            mockMvc.perform(delete("/experiences/" + experienceId)
                            .with(SecurityMockMvcRequestPostProcessors.user(user)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.data").value("Experience with id " + experienceId + " deleted"));
        }

        @Test
        void testDeleteExperience_NotFound() throws Exception {
            Long experienceId = 999L;

            doThrow(new EntityNotFoundException("Experience not found")).when(experienceService).deleteById(experienceId);

            mockMvc.perform(delete("/experiences/" + experienceId)
                            .with(SecurityMockMvcRequestPostProcessors.user(user)))
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.errors").value("Experience not found"));
        }

        @Test
        void testDeleteExperience_Unauthorized() throws Exception {
            Long experienceId = 1L;

            mockMvc = MockMvcBuilders.standaloneSetup(experienceController)
                    .setMessageConverters(new MappingJackson2HttpMessageConverter(objectMapper))
                    .setCustomArgumentResolvers(new PrincipalDetailsArgumentResolver(null))
                    .build();

            mockMvc.perform(delete("/experiences/" + experienceId))
                    .andExpect(status().isUnauthorized());
        }
    }

    @Nested
    class GetExperiencesByTalentIdTest {

        @Test
        void testGetExperiencesByTalentId_Success() throws Exception {
            // Given
            String talentId = "1";
            List<ExperienceResponseDTO> mockResponseList = List.of(
                    createMockResponseDTO(),
                    ExperienceResponseDTO.builder()
                            .id(2L)
                            .title("Software Engineer")
                            .company("Tech Corp")
                            .employmentType(EmploymentType.FULL_TIME)
                            .startDate(LocalDate.now().minusYears(2))
                            .endDate(LocalDate.now().minusYears(1))
                            .location("Jakarta")
                            .locationType(LocationType.HYBRID)
                            .build()
            );

            when(experienceService.getByTalentId(talentId)).thenReturn(mockResponseList);

            // When & Then
            mockMvc.perform(get("/experiences/user/" + talentId)
                            .with(SecurityMockMvcRequestPostProcessors.user(user))
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.data").isArray())
                    .andExpect(jsonPath("$.data.length()").value(2))
                    .andExpect(jsonPath("$.data[0].id").value(1))
                    .andExpect(jsonPath("$.data[0].title").value("Lead Construction Project Manager"))
                    .andExpect(jsonPath("$.data[1].id").value(2))
                    .andExpect(jsonPath("$.data[1].title").value("Software Engineer"));

            verify(experienceService, times(1)).getByTalentId(talentId);
        }

        @Test
        void testGetExperiencesByTalentId_EmptyList() throws Exception {
            // Given
            String talentId = "999"; // Non-existent talent ID
            List<ExperienceResponseDTO> emptyList = List.of();

            when(experienceService.getByTalentId(talentId)).thenReturn(emptyList);

            // When & Then
            mockMvc.perform(get("/experiences/user/" + talentId)
                            .with(SecurityMockMvcRequestPostProcessors.user(user))
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.data").isArray())
                    .andExpect(jsonPath("$.data.length()").value(0));

            verify(experienceService, times(1)).getByTalentId(talentId);
        }

        @Test
        void testGetExperiencesByTalentId_InvalidTalentId() throws Exception {
            // Given
            String invalidTalentId = "invalid-format";

            when(experienceService.getByTalentId(invalidTalentId))
                    .thenThrow(new IllegalArgumentException("Invalid talent ID format"));

            // When & Then
            mockMvc.perform(get("/experiences/user/" + invalidTalentId)
                            .with(SecurityMockMvcRequestPostProcessors.user(user))
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.errors").value("Invalid talent ID format"));

            verify(experienceService, times(1)).getByTalentId(invalidTalentId);
        }

        @Test
        void testGetExperiencesByTalentId_ServiceException() throws Exception {
            // Given
            String talentId = "1";

            when(experienceService.getByTalentId(talentId))
                    .thenThrow(new RuntimeException("Service error occurred"));

            // When & Then
            mockMvc.perform(get("/experiences/user/" + talentId)
                            .with(SecurityMockMvcRequestPostProcessors.user(user))
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isInternalServerError())
                    .andExpect(jsonPath("$.errors").value("Service error occurred"));

            verify(experienceService, times(1)).getByTalentId(talentId);
        }

        @Test
        void testGetExperiencesByTalentId_Unauthorized() throws Exception {
            // Given
            String talentId = "1";

            // Create a MockMvc instance without authentication
            mockMvc = MockMvcBuilders.standaloneSetup(experienceController)
                    .setMessageConverters(new MappingJackson2HttpMessageConverter(objectMapper))
                    .setCustomArgumentResolvers(new PrincipalDetailsArgumentResolver(null))
                    .build();

            // When & Then
            mockMvc.perform(get("/experiences/user/" + talentId)
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isUnauthorized());
        }
    }
}