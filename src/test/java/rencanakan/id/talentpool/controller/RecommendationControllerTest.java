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
import org.springframework.security.access.AccessDeniedException;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.servlet.mvc.method.annotation.PrincipalMethodArgumentResolver;
import rencanakan.id.talentpool.dto.RecommendationResponseDTO;
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

    private RecommendationResponseDTO responseDTO;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
        mockMvc = MockMvcBuilders.standaloneSetup(recommendationController)
                .setMessageConverters(new MappingJackson2HttpMessageConverter(objectMapper))
                .setControllerAdvice(new ErrorController())
                .setCustomArgumentResolvers(new PrincipalMethodArgumentResolver())
                .build();

        id = "1";

        responseDTO = new RecommendationResponseDTO(
                id, "talentId", 123L, "contractorName", "Some message", StatusType.ACCEPTED
        );
    }

    @Test
    void testEditStatusById() throws Exception {
        when(recommendationService.editStatusById(any(String.class),any(String.class), any(StatusType.class))).thenReturn(responseDTO);

        mockMvc.perform(patch("/recommendations/{id}", id)
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(StatusType.ACCEPTED)))
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
        when(recommendationService.editStatusById(any(String.class),any(String.class),  any(StatusType.class)))
                .thenThrow(new EntityNotFoundException("Recommendation with ID " + id + " not found"));

        mockMvc.perform(patch("/recommendations/{id}", id)
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(StatusType.ACCEPTED)))
                .andExpect(status().isNotFound());
    }
    @Test
    public void testEditStatus_UserNotAuthorized_ReturnsForbidden() throws Exception {

        Long experienceId = 1L;

        when(recommendationService.editStatusById(any(String.class), any(String.class), any(StatusType.class)))
                .thenThrow(new AccessDeniedException("You are not allowed to edit this recommendation."));

        mockMvc.perform(patch("/recommendations/{id}", experienceId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(StatusType.ACCEPTED)))
                .andExpect(status().isForbidden()) // Expect 403 Forbidden status
                .andExpect(jsonPath("$.errors").value("You are not allowed to edit this recommendation.")); // Check error message
    }

    @Test
    void testEditStatusById_ModifiedResponse() throws Exception {
        RecommendationResponseDTO modifiedResponseDTO = new RecommendationResponseDTO(
                "2", "newTalentId", 456L, "newContractorName", "Modified message", StatusType.PENDING
        );

        when(recommendationService.editStatusById(any(String.class),any(String.class), any(StatusType.class))).thenReturn(modifiedResponseDTO);

        mockMvc.perform(patch("/recommendations/{id}", id)
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(StatusType.ACCEPTED)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.id").value(modifiedResponseDTO.getId()))
                .andExpect(jsonPath("$.data.talentId").value(modifiedResponseDTO.getTalentId()))
                .andExpect(jsonPath("$.data.contractorId").value(modifiedResponseDTO.getContractorId()))
                .andExpect(jsonPath("$.data.contractorName").value(modifiedResponseDTO.getContractorName()))
                .andExpect(jsonPath("$.data.message").value(modifiedResponseDTO.getMessage()))
                .andExpect(jsonPath("$.data.status").value(modifiedResponseDTO.getStatus().toString()))
                .andExpect(jsonPath("$.errors").isEmpty());
    }
}
