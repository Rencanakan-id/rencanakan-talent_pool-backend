package rencanakan.id.talentpool.controller;

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
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import rencanakan.id.talentpool.dto.ExperienceResponseDTO;
import rencanakan.id.talentpool.enums.EmploymentType;
import rencanakan.id.talentpool.enums.LocationType;
import rencanakan.id.talentpool.service.ExperienceServiceImpl;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
public class ExperienceControllerTest {

    @Mock
    private ExperienceServiceImpl experienceService;

    @InjectMocks
    private ExperienceController experienceController;

    private MockMvc mockMvc;

    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        objectMapper = Jackson2ObjectMapperBuilder.json()
                .modules(new JavaTimeModule())
                .featuresToDisable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
                .build();

        mockMvc = MockMvcBuilders.standaloneSetup(experienceController)
                .setControllerAdvice(new ErrorController())
                .setMessageConverters(new MappingJackson2HttpMessageConverter(objectMapper))
                .build();
    }

    private ExperienceResponseDTO createExperienceResponse(Long id, String title, String company) {
        return ExperienceResponseDTO.builder()
                .id(id)
                .title(title)
                .company(company)
                .employmentType(EmploymentType.FULL_TIME)
                .startDate(LocalDate.now().minusMonths(6))
                .endDate(LocalDate.now())
                .location("Jakarta")
                .locationType(LocationType.ON_SITE)
                .build();
    }

    @Nested
    class GetExperiencesByTalentIdTest {

        @Test
        void testGetExperiencesByTalentId_Success() throws Exception {
            // Arrange
            String talentId = "talent-123";
            String token = "Bearer sample-token";

            ExperienceResponseDTO experience1 = createExperienceResponse(1L, "Lead Construction Project Manager", "Aman");
            ExperienceResponseDTO experience2 = createExperienceResponse(2L, "Software Engineer", "Tech Corp");

            List<ExperienceResponseDTO> experiences = Arrays.asList(experience1, experience2);

            when(experienceService.getByTalentId(talentId)).thenReturn(experiences);

            // Act & Assert
            mockMvc.perform(get("/experiences/user/" + talentId)
                            .header("Authorization", token)
                            .contentType(MediaType.APPLICATION_JSON))
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.data").isArray())
                    .andExpect(jsonPath("$.data.length()").value(2))
                    .andExpect(jsonPath("$.data[0].id").value(1L))
                    .andExpect(jsonPath("$.data[0].title").value("Lead Construction Project Manager"))
                    .andExpect(jsonPath("$.data[1].id").value(2L))
                    .andExpect(jsonPath("$.data[1].title").value("Software Engineer"));

            verify(experienceService, times(1)).getByTalentId(talentId);
        }

        @Test
        void testGetExperiencesByTalentId_EmptyList() throws Exception {
            // Arrange
            String talentId = "talent-456";
            String token = "Bearer sample-token";

            when(experienceService.getByTalentId(talentId)).thenReturn(Collections.emptyList());

            // Act & Assert
            mockMvc.perform(get("/experiences/user/" + talentId)
                            .header("Authorization", token)
                            .contentType(MediaType.APPLICATION_JSON))
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.data").isArray())
                    .andExpect(jsonPath("$.data.length()").value(0));

            verify(experienceService, times(1)).getByTalentId(talentId);
        }

        @Test
        void testGetExperiencesByTalentId_EntityNotFoundException() throws Exception {
            // Arrange
            String talentId = "non-existent-talent";
            String token = "Bearer sample-token";

            when(experienceService.getByTalentId(talentId))
                    .thenThrow(new EntityNotFoundException("Talent with id " + talentId + " not found"));

            // Act & Assert
            mockMvc.perform(get("/experiences/user/" + talentId)
                            .header("Authorization", token)
                            .contentType(MediaType.APPLICATION_JSON))
                    .andDo(print())
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.errors").value("Talent with id " + talentId + " not found"))
                    .andExpect(jsonPath("$.data").doesNotExist());

            verify(experienceService, times(1)).getByTalentId(talentId);
        }

        @Test
        void testGetExperiencesByTalentId_MissingAuthorizationHeader() throws Exception {
            // Arrange
            String talentId = "talent-123";

            // Act & Assert
            mockMvc.perform(get("/experiences/user/" + talentId)
                            .contentType(MediaType.APPLICATION_JSON))
                    .andDo(print())
                    .andExpect(status().isUnauthorized())
                    .andExpect(jsonPath("$.errors").value("Unauthorized"));

            verify(experienceService, never()).getByTalentId(anyString());
        }
    }
}