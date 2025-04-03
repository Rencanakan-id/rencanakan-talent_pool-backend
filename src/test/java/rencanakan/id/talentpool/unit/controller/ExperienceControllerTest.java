package rencanakan.id.talentpool.unit.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

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
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import jakarta.persistence.EntityNotFoundException;
import rencanakan.id.talentpool.controller.ExperienceController;
import rencanakan.id.talentpool.dto.ExperienceRequestDTO;
import rencanakan.id.talentpool.dto.ExperienceResponseDTO;
import rencanakan.id.talentpool.enums.EmploymentType;
import rencanakan.id.talentpool.enums.LocationType;
import rencanakan.id.talentpool.model.User;
import rencanakan.id.talentpool.service.ExperienceServiceImpl;

@ExtendWith(MockitoExtension.class)
class ExperienceControllerTest {

    @Mock
    private ExperienceServiceImpl experienceService;

    @InjectMocks
    private ExperienceController experienceController;

    private MockMvc mockMvc;

    private ObjectMapper objectMapper;

    private User user;

    @BeforeEach
    void setUp() {
        objectMapper = Jackson2ObjectMapperBuilder.json()
                .modules(new JavaTimeModule())
                .featuresToDisable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
                .build();
        
        user = createUser();

        mockMvc = MockMvcBuilders.standaloneSetup(experienceController)
                .setMessageConverters(new MappingJackson2HttpMessageConverter(objectMapper))
                .setCustomArgumentResolvers(new PrincipalDetailsArgumentResolver(user))
                .build();
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
    class EditExperienceTest {

        @Test
        void testEditExperience_Success() throws Exception {
            Long experienceId = 1L;
            ExperienceRequestDTO request = createValidRequestDTO();
            ExperienceResponseDTO mockResponseDTO = createMockResponseDTO();

            String expectedStartDate = mockResponseDTO.getStartDate().format(DateTimeFormatter.ISO_LOCAL_DATE);
            String expectedEndDate = mockResponseDTO.getEndDate().format(DateTimeFormatter.ISO_LOCAL_DATE);

            when(experienceService.editById(eq(experienceId), any(ExperienceRequestDTO.class)))
                    .thenReturn(mockResponseDTO);

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
        void testEditExperience_BadRequest() throws Exception {
            Long experienceId = 1L;
            ExperienceRequestDTO invalidRequest = new ExperienceRequestDTO();

            mockMvc.perform(put("/experiences/" + experienceId)
                            .header("Authorization", "Bearer sample-token")
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
}