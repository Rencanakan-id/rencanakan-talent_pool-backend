package rencanakan.id.talentpool.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
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
import rencanakan.id.talentpool.dto.ExperienceRequestDTO;
import rencanakan.id.talentpool.enums.EmploymentType;
import rencanakan.id.talentpool.enums.LocationType;
import rencanakan.id.talentpool.model.Experience;
import rencanakan.id.talentpool.service.ExperienceServiceImpl;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

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
                .setMessageConverters(new MappingJackson2HttpMessageConverter(objectMapper))
                .build();
    }

    private ExperienceRequestDTO createValidRequestDTO() {
        ExperienceRequestDTO dto = new ExperienceRequestDTO();
        dto.setTitle("Lead Construction Project Manager");
        dto.setCompany("Aman");
        dto.setEmploymentType(EmploymentType.FULL_TIME);
        dto.setStartDate(LocalDate.now());
        dto.setEndDate(LocalDate.now().plusDays(1));
        dto.setLocation("Depok");
        dto.setLocationType(LocationType.ON_SITE);
        dto.setTalentId(1L);
        return dto;
    }

    private Experience createMockExperience() {
        return Experience.builder()
                .title("Lead Construction Project Manager")
                .company("Aman")
                .employmentType(EmploymentType.FULL_TIME)
                .startDate(LocalDate.now())
                .endDate(LocalDate.now().plusDays(1))
                .location("Depok")
                .locationType(LocationType.ON_SITE)
                .talentId(1L)
                .build();
    }

    @Nested
    class CreateExperienceTest {
        @Test
        void testCreateExperience_Success() throws Exception {
            ExperienceRequestDTO request = createValidRequestDTO();
            Experience mockSavedExperience = createMockExperience();

            String expectedStartDate = mockSavedExperience.getStartDate().format(DateTimeFormatter.ISO_LOCAL_DATE);
            String expectedEndDate = mockSavedExperience.getEndDate().format(DateTimeFormatter.ISO_LOCAL_DATE);

            when(experienceService.createExperience(any(ExperienceRequestDTO.class)))
                    .thenReturn(mockSavedExperience);

            mockMvc.perform(post("/experience")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request))
                    )
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.title").value("Lead Construction Project Manager"))
                    .andExpect(jsonPath("$.company").value("Aman"))
                    .andExpect(jsonPath("$.employmentType").value("FULL_TIME"))
                    .andExpect(jsonPath("$.startDate").value(expectedStartDate))
                    .andExpect(jsonPath("$.endDate").value(expectedEndDate))
                    .andExpect(jsonPath("$.location").value("Depok"))
                    .andExpect(jsonPath("$.locationType").value("ON_SITE"))
                    .andExpect(jsonPath("$.talentId").value(1L));
        }

        @Test
        void testCreateExperience_BadRequest() throws Exception {
            ExperienceRequestDTO request = new ExperienceRequestDTO();

            when(experienceService.createExperience(any(ExperienceRequestDTO.class)))
                    .thenThrow(new IllegalArgumentException("Invalid experience data"));

            mockMvc.perform(post("/experience")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request))
                    )
                    .andExpect(status().isBadRequest());
        }
    }
}