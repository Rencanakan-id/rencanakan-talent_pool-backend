package rencanakan.id.talentpool.controller;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import rencanakan.id.talentpool.dto.RecommendationResponseDTO;
import rencanakan.id.talentpool.dto.RecommendationStatusRequestDTO;
import rencanakan.id.talentpool.service.RecommendationService;
import rencanakan.id.talentpool.enums.StatusType;

@ExtendWith(MockitoExtension.class)
class RecommendationControllerTest {

    @Mock
    private RecommendationService recommendationService;

    @InjectMocks
    private RecommendationController recommendationController;

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    private String id;
    private RecommendationStatusRequestDTO requestDTO;
    private RecommendationResponseDTO responseDTO;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
        mockMvc = MockMvcBuilders.standaloneSetup(recommendationController)
                .setMessageConverters(new MappingJackson2HttpMessageConverter(objectMapper))
                .setControllerAdvice(new ErrorController())
                .build();

        id = "1";
        requestDTO = new RecommendationStatusRequestDTO(StatusType.ACCEPTED);
        responseDTO = new RecommendationResponseDTO(
                id, "talentId", 123L, "contractorName", "Some message", StatusType.ACCEPTED
        );
    }

    @Test
    void testEditStatusById() throws Exception {
        when(recommendationService.editStatusById(eq(id), any(RecommendationStatusRequestDTO.class))).thenReturn(responseDTO);

        mockMvc.perform(patch("/recommendations/{id}", id)
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.id").value(responseDTO.getId()))
                .andExpect(jsonPath("$.data.talentId").value(responseDTO.getTalentId()))
                .andExpect(jsonPath("$.data.contractorId").value(responseDTO.getContractorId()))
                .andExpect(jsonPath("$.data.contractorName").value(responseDTO.getContractorName()))
                .andExpect(jsonPath("$.data.message").value(responseDTO.getMessage()))
                .andExpect(jsonPath("$.data.status").value(responseDTO.getStatus().toString()))
                .andExpect(jsonPath("$.errors").isEmpty());
    }

    @Test
    void testEditStatusById_RecommendationNotFound() throws Exception {
        when(recommendationService.editStatusById(eq(id),  any(RecommendationStatusRequestDTO.class)))
                .thenThrow(new EntityNotFoundException("Recommendation with ID " + id + " not found"));

        mockMvc.perform(patch("/recommendations/{id}", id)
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isNotFound());
    }

    @Test
    void testEditStatusById_ModifiedResponse() throws Exception {
        RecommendationResponseDTO modifiedResponseDTO = new RecommendationResponseDTO(
                "2", "newTalentId", 456L, "newContractorName", "Modified message", StatusType.PENDING
        );

        when(recommendationService.editStatusById(eq(id), any(RecommendationStatusRequestDTO.class))).thenReturn(modifiedResponseDTO);

        mockMvc.perform(patch("/recommendations/{id}", id)
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.id").value(modifiedResponseDTO.getId()))
                .andExpect(jsonPath("$.data.talentId").value(modifiedResponseDTO.getTalentId()))
                .andExpect(jsonPath("$.data.contractorId").value(modifiedResponseDTO.getContractorId()))
                .andExpect(jsonPath("$.data.contractorName").value(modifiedResponseDTO.getContractorName()))
                .andExpect(jsonPath("$.data.message").value(modifiedResponseDTO.getMessage()))
                .andExpect(jsonPath("$.data.status").value(modifiedResponseDTO.getStatus().toString()))
                .andExpect(jsonPath("$.errors").isEmpty());
    }

    @Test
    void deleteByStatusId_NotFound_ShouldReturn404() throws Exception {
        String notFoundId = "999";
        doThrow(new EntityNotFoundException("Recommendation with id " + notFoundId + " not found."))
                .when(recommendationService).deleteById(notFoundId);

        mockMvc.perform(delete("/recommendations/{id}", notFoundId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.errors").value("Recommendation with id " + notFoundId + " not found."));

    }
    @Test
    void deleteByStatusId_Success_ShouldReturn200() throws Exception {
        when(recommendationService.deleteById(id)).thenReturn(responseDTO);
        mockMvc.perform(delete("/recommendations/{id}", id)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk()) // Harus 200 OK
                .andExpect(jsonPath("$.data.id").value(id))
                .andExpect(jsonPath("$.data.talentId").value("talentId"))
                .andExpect(jsonPath("$.data.contractorId").value(123L))
                .andExpect(jsonPath("$.data.contractorName").value("contractorName"))
                .andExpect(jsonPath("$.data.message").value("Some message"))
                .andExpect(jsonPath("$.data.status").value("ACCEPTED"));

    }
}
