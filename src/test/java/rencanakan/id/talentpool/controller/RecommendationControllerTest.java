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
import rencanakan.id.talentpool.dto.*;
import rencanakan.id.talentpool.enums.EmploymentType;
import rencanakan.id.talentpool.enums.LocationType;
import rencanakan.id.talentpool.enums.StatusType;
import rencanakan.id.talentpool.model.Recommendation;
import rencanakan.id.talentpool.model.User;
import rencanakan.id.talentpool.service.ExperienceServiceImpl;
import rencanakan.id.talentpool.service.RecommendationServiceImpl;

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

public class RecommendationControllerTest {

    @Mock
    private RecommendationServiceImpl recommendationService;

    @InjectMocks
    private RecommendationController recommendationController;

    private MockMvc mockMvc;

    private ObjectMapper mapper;

    private User mockTalent;

    @BeforeEach
    void setUp() {
        mapper = Jackson2ObjectMapperBuilder.json()
                .modules(new JavaTimeModule())
                .featuresToDisable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
                .build();

        mockMvc = MockMvcBuilders.standaloneSetup(recommendationController)
                .setMessageConverters(new MappingJackson2HttpMessageConverter(mapper))
                .build();

        mockTalent = User.builder()
                .id("user123")
                .firstName("Test")
                .lastName("Talent")
                .email("test@example.com")
                .password("Password123")
                .phoneNumber("081234567890")
                .nik("1234567890123456")
                .build();
    }

    private RecommendationRequestDTO createValidRequestDTO() {
        return RecommendationRequestDTO.builder()
                .contractorId(1L)
                .contractorName("Contractor name")
                .message("Test controller message")
                .status(StatusType.PENDING)
                .build();
    }

    private RecommendationResponseDTO createMockResponseDTO() {
        return RecommendationResponseDTO.builder()
                .id("recommendation123")
                .talent(mockTalent)
                .contractorId(1L)
                .contractorName("Contractor name")
                .message("Test controller message")
                .status(StatusType.PENDING)
                .build();
    }

    @Nested
    class CreateRecommendationTest {

        @Test
        void testCreateRecommendation_Success() throws Exception {
            RecommendationRequestDTO request = createValidRequestDTO();
            RecommendationResponseDTO mockResponseDTO = createMockResponseDTO();

            when(recommendationService.createRecommendation(mockTalent, any(RecommendationRequestDTO.class)))
                    .thenReturn(mockResponseDTO);

            mockMvc.perform(post("/recommendations")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(mapper.writeValueAsString(request)))
                    .andExpect(jsonPath("$.data.id").value("recommendation123"))
                    .andExpect(jsonPath("$.data.talent").value(mockTalent))
                    .andExpect(jsonPath("$.data.contractorId").value(1L))
                    .andExpect(jsonPath("$.data.contractorName").value("Contractor name"))
                    .andExpect(jsonPath("$.data.message").value("Test controller message"))
                    .andExpect(jsonPath("$.data.status").value(StatusType.PENDING));
        }

        @Test
        void testCreateRecommendation_BadRequest() throws Exception {
            RecommendationRequestDTO invalidRequest = new RecommendationRequestDTO();

            mockMvc.perform(post("/experience")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(mapper.writeValueAsString(invalidRequest)))
                    .andExpect(status().isBadRequest());
        }
    }
}

