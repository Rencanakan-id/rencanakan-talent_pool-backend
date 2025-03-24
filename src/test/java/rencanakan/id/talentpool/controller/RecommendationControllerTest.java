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
import rencanakan.id.talentpool.dto.RecommendationRequestDTO;
import rencanakan.id.talentpool.dto.RecommendationResponseDTO;
import rencanakan.id.talentpool.enums.StatusType;
import rencanakan.id.talentpool.service.RecommendationServiceImpl;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
public class RecommendationControllerTest {
    @Mock
    private RecommendationServiceImpl recommendationService;

    @InjectMocks
    private RecommendationController recommendationController;

    private MockMvc mockMvc;

    private ObjectMapper objectMapper;

    private RecommendationResponseDTO response;
    private RecommendationRequestDTO request;


    @BeforeEach
    void setUp() {
        objectMapper = Jackson2ObjectMapperBuilder.json()
                .modules(new JavaTimeModule())
                .featuresToDisable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
                .build();

        mockMvc = MockMvcBuilders.standaloneSetup(recommendationController)
                .setMessageConverters(new MappingJackson2HttpMessageConverter(objectMapper))
                .setControllerAdvice(new ErrorController())
                .build();
        request = createRecommendationRequestDTO();
        response= createRecommendationResponseDTO();
    }


    RecommendationRequestDTO createRecommendationRequestDTO() {
        return RecommendationRequestDTO.builder()
                .message("hehe")
                .status(StatusType.ACCEPTED)
                .contractorId(1L)
                .contractorName("contractor")
                .build();
    }

    RecommendationResponseDTO createRecommendationResponseDTO() {
        return RecommendationResponseDTO.builder()
                .message("keren")
                .build();
    }

    @Nested
    class EditRecommendationTest {

        @Test
        void testEditExperience_Success() throws Exception {
            Long recommendationId = 1L;

            when(recommendationService.editById(eq(recommendationId), any(RecommendationRequestDTO.class)))
                    .thenReturn(response);

            mockMvc.perform(put("/recommendations/" + recommendationId)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.data.message").value("keren"));
        }

        @Test
        void testValidationError() throws Exception {
            String token = "Bearer sample-token";
            long id = 1L;
            request.setMessage(null);

            mockMvc.perform(put("/recommendations/" + id)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.errors").exists());
        }
    }


}
