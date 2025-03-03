package rencanakan.id.talentPool.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import rencanakan.id.talentPool.dto.EditExperienceRequestDTO;
import rencanakan.id.talentPool.dto.ExperienceListResponseDTO;
import rencanakan.id.talentPool.dto.ExperienceResponseDTO;
import rencanakan.id.talentPool.enums.EmploymentType;
import rencanakan.id.talentPool.enums.LocationType;
import rencanakan.id.talentPool.service.ExperienceService;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ExperienceController.class)
public class ExperienceControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ExperienceService experienceService;

    private final ObjectMapper objectMapper = new ObjectMapper().registerModule(new JavaTimeModule());

    @Test
    public void testGetExperiencesByTalentId_Success() throws Exception {
        // Arrange
        Long talentId = 10L;
        String token = "Bearer sample-token";

        // Create response DTOs
        ExperienceResponseDTO exp1 = new ExperienceResponseDTO();
        exp1.setId(1L);
        exp1.setTitle("Software Engineer");
        exp1.setCompany("Tech Corp");
        exp1.setEmploymentType(EmploymentType.FULL_TIME);
        exp1.setStartDate(LocalDate.of(2022, 1, 1));
        exp1.setEndDate(LocalDate.of(2023, 1, 1));
        exp1.setLocation("Jakarta");
        exp1.setLocationType(LocationType.HYBRID);
        exp1.setTalentId(talentId);

        ExperienceResponseDTO exp2 = new ExperienceResponseDTO();
        exp2.setId(2L);
        exp2.setTitle("Product Manager");
        exp2.setCompany("Digital Solutions");
        exp2.setEmploymentType(EmploymentType.PART_TIME);
        exp2.setStartDate(LocalDate.of(2021, 3, 1));
        exp2.setEndDate(LocalDate.of(2022, 4, 1));
        exp2.setLocation("Bandung");
        exp2.setLocationType(LocationType.HYBRID);
        exp2.setTalentId(talentId);

        List<ExperienceResponseDTO> experiences = Arrays.asList(exp1, exp2);
        ExperienceListResponseDTO responseDTO = new ExperienceListResponseDTO(experiences);

        when(experienceService.getByTalentId(eq(talentId))).thenReturn(responseDTO);

        // Act & Assert
        mockMvc.perform(get("/api/experiences/" + talentId)
                        .header("Authorization", token)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").exists())
                .andExpect(jsonPath("$.data.experiences").isArray())
                .andExpect(jsonPath("$.data.experiences.length()").value(2))
                .andExpect(jsonPath("$.data.experiences[0].id").value(exp1.getId()))
                .andExpect(jsonPath("$.data.experiences[0].title").value(exp1.getTitle()))
                .andExpect(jsonPath("$.data.experiences[0].company").value(exp1.getCompany()))
                .andExpect(jsonPath("$.data.experiences[0].employmentType").value(exp1.getEmploymentType().toString()))
                .andExpect(jsonPath("$.data.experiences[0].startDate").value(exp1.getStartDate().toString()))
                .andExpect(jsonPath("$.data.experiences[0].endDate").value(exp1.getEndDate().toString()))
                .andExpect(jsonPath("$.data.experiences[0].location").value(exp1.getLocation()))
                .andExpect(jsonPath("$.data.experiences[0].locationType").value(exp1.getLocationType().toString()))
                .andExpect(jsonPath("$.data.experiences[0].talentId").value(exp1.getTalentId()))
                .andExpect(jsonPath("$.data.experiences[1].id").value(exp2.getId()))
                .andExpect(jsonPath("$.data.experiences[1].title").value(exp2.getTitle()));

        verify(experienceService, times(1)).getByTalentId(talentId);
    }

    @Test
    public void testGetExperiencesByTalentId_EntityNotFoundException() throws Exception {
        // Arrange
        Long talentId = 999L;
        String token = "Bearer sample-token";

        when(experienceService.getByTalentId(eq(talentId)))
                .thenThrow(new EntityNotFoundException("Experience is empty"));

        // Act & Assert
        mockMvc.perform(get("/api/experiences/" + talentId)
                        .header("Authorization", token)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.errors").value("Experience is empty"));

        verify(experienceService, times(1)).getByTalentId(talentId);
    }

    @Test
    public void testGetExperiencesByTalentId_MissingAuthorizationHeader() throws Exception {
        // Arrange
        Long talentId = 10L;

        // Act & Assert - No Authorization header
        mockMvc.perform(get("/api/experiences/" + talentId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized());

        // Verify service was never called
        verify(experienceService, never()).getByTalentId(anyLong());
    }

    // Existing tests from the original file
    @Test
    public void testEditExperienceById_Success() throws Exception {
        Long experienceId = 1L;
        String token = "Bearer sample-token";

        EditExperienceRequestDTO requestDTO = new EditExperienceRequestDTO();
        requestDTO.setTitle("Software Engineer");
        requestDTO.setCompany("Tech Corp");
        requestDTO.setEmploymentType(EmploymentType.FULL_TIME);
        requestDTO.setStartDate(LocalDate.of(2022, 1, 1));
        requestDTO.setEndDate(LocalDate.of(2023, 1, 1));
        requestDTO.setLocation("Jakarta");
        requestDTO.setLocationType(LocationType.HYBRID);
        requestDTO.setTalentId(10L);

        ExperienceResponseDTO responseDTO = new ExperienceResponseDTO();
        responseDTO.setId(experienceId);
        responseDTO.setTitle(requestDTO.getTitle());
        responseDTO.setCompany(requestDTO.getCompany());
        responseDTO.setEmploymentType(requestDTO.getEmploymentType());
        responseDTO.setStartDate(requestDTO.getStartDate());
        responseDTO.setEndDate(requestDTO.getEndDate());
        responseDTO.setLocation(requestDTO.getLocation());
        responseDTO.setLocationType(requestDTO.getLocationType());
        responseDTO.setTalentId(requestDTO.getTalentId());

        when(experienceService.editById(eq(experienceId), any(EditExperienceRequestDTO.class)))
                .thenReturn(responseDTO);

        mockMvc.perform(put("/api/experiences/" + experienceId)
                        .header("Authorization", token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.id").value(responseDTO.getId()))
                .andExpect(jsonPath("$.data.title").value(responseDTO.getTitle()))
                .andExpect(jsonPath("$.data.company").value(responseDTO.getCompany()))
                .andExpect(jsonPath("$.data.employmentType").value(responseDTO.getEmploymentType().toString()))
                .andExpect(jsonPath("$.data.startDate").value(responseDTO.getStartDate().toString()))
                .andExpect(jsonPath("$.data.endDate").value(responseDTO.getEndDate().toString()))
                .andExpect(jsonPath("$.data.location").value(responseDTO.getLocation()))
                .andExpect(jsonPath("$.data.locationType").value(responseDTO.getLocationType().toString()))
                .andExpect(jsonPath("$.data.talentId").value(responseDTO.getTalentId()));
    }

    @Test
    void testValidationError() throws Exception {
        String token = "Bearer sample-token";
        Long id = 1L;
        EditExperienceRequestDTO invalidRequest = new EditExperienceRequestDTO();
        invalidRequest.setTitle("Software Engineer");

        mockMvc.perform(put("/api/experiences/" + id)
                        .header("Authorization", token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors").exists());
    }

    @Test
    void testEntityNotFoundException() throws Exception {
        EditExperienceRequestDTO requestDTO = new EditExperienceRequestDTO();
        requestDTO.setTitle("Software Engineer");
        requestDTO.setCompany("Tech Corp");
        requestDTO.setEmploymentType(EmploymentType.FULL_TIME);
        requestDTO.setStartDate(LocalDate.of(2022, 1, 1));
        requestDTO.setEndDate(LocalDate.of(2023, 1, 1));
        requestDTO.setLocation("Jakarta");
        requestDTO.setLocationType(LocationType.HYBRID);
        requestDTO.setTalentId(10L);
        Long invalidId = 999L;
        String token = "Bearer sample-token";

        when(experienceService.editById(eq(invalidId), any(EditExperienceRequestDTO.class)))
                .thenThrow(new EntityNotFoundException("Experience Not Found"));

        mockMvc.perform(put("/api/experiences/" + invalidId)
                        .header("Authorization", token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.errors").value("Experience Not Found"));
    }

    @Test
    void testMissingAuthorizationHeader() throws Exception {
        Long id = 1L;
        EditExperienceRequestDTO validRequest = new EditExperienceRequestDTO();
        validRequest.setTitle("Software Engineer");
        validRequest.setCompany("Tech Corp");
        validRequest.setStartDate(LocalDate.of(2022, 1, 1));
        validRequest.setEndDate(LocalDate.of(2024, 12, 31));

        mockMvc.perform(put("/api/experiences/" + id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validRequest)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void testDeleteExperienceById_Success() throws Exception {
        Long id = 1L;
        String token = "Bearer sample-token";

        doNothing().when(experienceService).deleteById(id);

        mockMvc.perform(delete("/api/experiences/" + id)
                        .header("Authorization", token)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").value("Experience with id " + id + " deleted"));

        verify(experienceService, times(1)).deleteById(id);
    }

    @Test
    public void testGetDeleteByTalentId_MissingAuthorizationHeader() throws Exception {
        // Arrange
        Long id = 10L;

        // Act & Assert - No Authorization header
        mockMvc.perform(delete("/api/experiences/" + id)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized());

        // Verify service was never called
        verify(experienceService, never()).getByTalentId(anyLong());
    }
}