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
import rencanakan.id.talentpool.dto.ExperienceResponseDTO;
import rencanakan.id.talentpool.enums.EmploymentType;
import rencanakan.id.talentpool.enums.LocationType;
import rencanakan.id.talentpool.service.ExperienceServiceImpl;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.never;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
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
            .setMessageConverters(new MappingJackson2HttpMessageConverter(objectMapper))
            .build();
        
        
    }

    private ExperienceRequestDTO createExperienceRequestDTO() {
        return ExperienceRequestDTO.builder()
            .title("Lead Construction Project Manager")
            .company("Aman")
            .employmentType(EmploymentType.FULL_TIME)
            .startDate(LocalDate.now())
            .endDate(LocalDate.now().plusDays(1))
            .location("Depok")
            .locationType(LocationType.ON_SITE)
            .build();
    }

    private ExperienceResponseDTO createExperienceResponseDTO() {
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
            ExperienceRequestDTO request = createExperienceRequestDTO();
            ExperienceResponseDTO response = createExperienceResponseDTO();

            String expectedStartDate = response.getStartDate().format(DateTimeFormatter.ISO_LOCAL_DATE);
            String expectedEndDate = response.getEndDate().format(DateTimeFormatter.ISO_LOCAL_DATE);

            when(experienceService.createExperience(any(ExperienceRequestDTO.class)))
                .thenReturn(response);

            mockMvc.perform(post("/experiences")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
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
            ExperienceRequestDTO request = createExperienceRequestDTO();
            ExperienceResponseDTO response = createExperienceResponseDTO();

            String expectedStartDate = response.getStartDate().format(DateTimeFormatter.ISO_LOCAL_DATE);
            String expectedEndDate = response.getEndDate().format(DateTimeFormatter.ISO_LOCAL_DATE);

            when(experienceService.editById(eq(experienceId), any(ExperienceRequestDTO.class)))
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
        void testDeleteExperienceById_Success() throws Exception {
            Long id = 1L;
            String token = "Bearer sample-token";

            doNothing().when(experienceService).deleteById(id);

            mockMvc.perform(delete("/experiences/" + id)
                        .header("Authorization", token)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").value("Experience with id " + id + " deleted"));

            verify(experienceService, times(1)).deleteById(id);
        }
        
        @Test
        void testDeleteExperienceById_BadRequest() throws Exception {
            Long id = 1L;
            
            mockMvc.perform(delete("/experiences/" + id)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andDo(print());
            
            verify(experienceService, never()).deleteById(anyLong());
        }
        
        @Test
        void testDeleteExperienceById_InvalidId() throws Exception {
            String invalidId = "abc";
            String token = "Bearer sample-token";
            
            mockMvc.perform(delete("/experiences/" + invalidId)
                        .header("Authorization", token)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
            
            verify(experienceService, never()).deleteById(anyLong());
        }
    }
}