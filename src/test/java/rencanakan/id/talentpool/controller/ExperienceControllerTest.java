package rencanakan.id.talentpool.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import rencanakan.id.talentpool.dto.EditExperienceRequestDTO;
import rencanakan.id.talentpool.dto.ExperienceResponseDTO;
import rencanakan.id.talentpool.enums.EmploymentType;
import rencanakan.id.talentpool.enums.LocationType;
import rencanakan.id.talentpool.service.ExperienceService;

import java.time.LocalDate;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@WebMvcTest(ExperienceController.class)
public class ExperienceControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private ExperienceService experienceService;
    private final ObjectMapper objectMapper = new ObjectMapper().registerModule(new JavaTimeModule());

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
                        .header("Authorization", token)  // Tambahkan header Authorization
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.errors").value("Experience Not Found"));  // Pastikan pesan sesuai dengan exception
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
                        .content(objectMapper.writeValueAsString(validRequest))) // ❌ Tidak ada Authorization header
                .andExpect(status().isUnauthorized());
    }


}
