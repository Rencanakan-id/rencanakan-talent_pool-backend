package rencanakan.id.talentpool.unit.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import rencanakan.id.talentpool.controller.RecommendationController;
import rencanakan.id.talentpool.dto.RecommendationResponseDTO;
import rencanakan.id.talentpool.enums.StatusType;
import rencanakan.id.talentpool.model.User;
import rencanakan.id.talentpool.service.RecommendationService;

import java.util.Arrays;
import java.util.Collections;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class RecommendationControllerTest {

    private MockMvc mockMvc;

    @Mock
    private RecommendationService recommendationService;

    @InjectMocks
    private RecommendationController recommendationController;

    private void setupUnauthorizedMockMvc() {
        mockMvc = MockMvcBuilders
                .standaloneSetup(recommendationController)
                .setCustomArgumentResolvers(new PrincipalDetailsArgumentResolver(null))
                .build();
    }

    @Nested
    class ReadRecommendationTests {
        private User testUser;
        private RecommendationResponseDTO recommendation1;
        private RecommendationResponseDTO recommendation2;
        private RecommendationResponseDTO recommendation3;
        private Map<StatusType, List<RecommendationResponseDTO>> groupedRecommendations;
    
        @BeforeEach
        void setUp() {
            testUser = new User();
            testUser.setId("user-id-1");
            testUser.setEmail("john.doe@email.com");
    
            mockMvc = MockMvcBuilders
                    .standaloneSetup(recommendationController)
                    .setCustomArgumentResolvers(new PrincipalDetailsArgumentResolver(testUser))
                    .build();
    
            setUpRecommendations();
        }
    
        private void setUpRecommendations() {
            recommendation1 = new RecommendationResponseDTO();
            recommendation1.setId("rec-id-1");
            recommendation1.setTalentId("user-id-1");
            recommendation1.setContractorId(101L);
            recommendation1.setContractorName("Contractor A");
            recommendation1.setMessage("Excellent performance on project X");
            recommendation1.setStatus(StatusType.PENDING);
    
            recommendation2 = new RecommendationResponseDTO();
            recommendation2.setId("rec-id-2");
            recommendation2.setTalentId("user-id-1");
            recommendation2.setContractorId(102L);
            recommendation2.setContractorName("Contractor B");
            recommendation2.setMessage("Great collaboration on project Y");
            recommendation2.setStatus(StatusType.ACCEPTED);
    
            recommendation3 = new RecommendationResponseDTO();
            recommendation3.setId("rec-id-3");
            recommendation3.setTalentId("user-id-2");
            recommendation3.setContractorId(103L);
            recommendation3.setContractorName("Contractor C");
            recommendation3.setMessage("Good technical skills");
            recommendation3.setStatus(StatusType.DECLINED);
    
            groupedRecommendations = new EnumMap<>(StatusType.class);
            groupedRecommendations.put(StatusType.PENDING, Collections.singletonList(recommendation1));
            groupedRecommendations.put(StatusType.ACCEPTED, Collections.singletonList(recommendation2));
            groupedRecommendations.put(StatusType.DECLINED, Collections.singletonList(recommendation3));
        }
        
        @Test
        void getRecommendationById_ExistingId_ReturnsRecommendation() throws Exception {
            String id = "rec-id-1";
            when(recommendationService.getById(id)).thenReturn(recommendation1);
            
            mockMvc.perform(get("/recommendations/{recommendationId}", id))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$.data.id", is(recommendation1.getId())))
                    .andExpect(jsonPath("$.data.talentId", is(recommendation1.getTalentId())))
                    .andExpect(jsonPath("$.data.contractorName", is(recommendation1.getContractorName())))
                    .andExpect(jsonPath("$.data.message", is(recommendation1.getMessage())))
                    .andExpect(jsonPath("$.data.status", is(recommendation1.getStatus().toString())));
    
            verify(recommendationService).getById(id);
        }
    
        @Test
        void getRecommendationsByTalentId_ExistingTalentId_ReturnsFilteredRecommendations() throws Exception {
            String talentId = "user-id-1";
            List<RecommendationResponseDTO> talent1Recommendations = Arrays.asList(recommendation1, recommendation2);
            when(recommendationService.getByTalentId(talentId)).thenReturn(talent1Recommendations);
            
            mockMvc.perform(get("/recommendations/user/{userId}", talentId))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$.data", hasSize(2)))
                    .andExpect(jsonPath("$.data[0].id", is(recommendation1.getId())))
                    .andExpect(jsonPath("$.data[1].id", is(recommendation2.getId())))
                    .andExpect(jsonPath("$.data[0].talentId", is(talentId)))
                    .andExpect(jsonPath("$.data[1].talentId", is(talentId)));
    
            verify(recommendationService).getByTalentId(talentId);
        }
    
        @Test
        void getRecommendationsByTalentIdAndStatus_ExistingCombination_ReturnsFilteredRecommendations() throws Exception {
            String talentId = "user-id-1";
            StatusType status = StatusType.PENDING;
            when(recommendationService.getByTalentIdAndStatus(talentId, status))
                    .thenReturn(Collections.singletonList(recommendation1));
            
            mockMvc.perform(get("/recommendations/user/{userId}/status/{status}", talentId, status))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$.data", hasSize(1)))
                    .andExpect(jsonPath("$.data[0].id", is(recommendation1.getId())))
                    .andExpect(jsonPath("$.data[0].talentId", is(talentId)))
                    .andExpect(jsonPath("$.data[0].status", is(status.toString())));
    
            verify(recommendationService).getByTalentIdAndStatus(talentId, status);
        }
    
        @Test
        void getRecommendationsByTalentIdGroupedByStatus_ExistingTalent_ReturnsGroupedRecommendations() throws Exception {
            String talentId = "user-id-1";
            Map<StatusType, List<RecommendationResponseDTO>> groupedByTalent = new EnumMap<>(StatusType.class);
            groupedByTalent.put(StatusType.PENDING, Collections.singletonList(recommendation1));
            groupedByTalent.put(StatusType.ACCEPTED, Collections.singletonList(recommendation2));
            groupedByTalent.put(StatusType.DECLINED, Collections.emptyList());
            
            when(recommendationService.getByTalentIdAndGroupedByStatus(talentId)).thenReturn(groupedByTalent);
            
            mockMvc.perform(get("/recommendations/user/{userId}/grouped-by-status", talentId))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$.data." + StatusType.PENDING, hasSize(1)))
                    .andExpect(jsonPath("$.data." + StatusType.ACCEPTED, hasSize(1)))
                    .andExpect(jsonPath("$.data." + StatusType.PENDING + "[0].id", is(recommendation1.getId())))
                    .andExpect(jsonPath("$.data." + StatusType.ACCEPTED + "[0].id", is(recommendation2.getId())));
    
            verify(recommendationService).getByTalentIdAndGroupedByStatus(talentId);
        }

        @Test
        void getRecommendationById_NotFound_ReturnsErrorResponse() throws Exception {
            String nonExistentId = "non-existent-id";
            when(recommendationService.getById(nonExistentId)).thenThrow(new IllegalArgumentException("Recommendation not found"));
            
            mockMvc.perform(get("/recommendations/{recommendationId}", nonExistentId))
                    .andExpect(status().isNotFound())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$.errors", containsString("Recommendation not found")));
    
            verify(recommendationService).getById(nonExistentId);
        }
        
        @Test
        void getRecommendationsByTalentId_EmptyResults_ReturnsNotFoundResponse() throws Exception {
            String talentId = "user-with-no-recommendations";
            when(recommendationService.getByTalentId(talentId)).thenReturn(Collections.emptyList());
            
            mockMvc.perform(get("/recommendations/user/{userId}", talentId))
                    .andExpect(status().isNotFound())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$.errors", containsString("No recommendations found")));
    
            verify(recommendationService).getByTalentId(talentId);
        }
        
        @Test
        void getRecommendationsByTalentIdAndStatus_EmptyResults_ReturnsNotFoundResponse() throws Exception {
            String talentId = "user-id-1";
            StatusType status = StatusType.DECLINED;
            when(recommendationService.getByTalentIdAndStatus(talentId, status))
                    .thenReturn(Collections.emptyList());
            
            mockMvc.perform(get("/recommendations/user/{userId}/status/{status}", talentId, status))
                    .andExpect(status().isNotFound())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$.errors", containsString("No recommendations found")));
    
            verify(recommendationService).getByTalentIdAndStatus(talentId, status);
        }
        
        @Test
        void getRecommendationsByTalentIdGroupedByStatus_EmptyResults_ReturnsNotFoundResponse() throws Exception {
            String talentId = "user-with-no-recommendations";
            Map<StatusType, List<RecommendationResponseDTO>> emptyMap = Collections.emptyMap();
            when(recommendationService.getByTalentIdAndGroupedByStatus(talentId)).thenReturn(emptyMap);
            
            mockMvc.perform(get("/recommendations/user/{userId}/grouped-by-status", talentId))
                    .andExpect(status().isNotFound())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$.errors", containsString("No recommendations found")));
    
            verify(recommendationService).getByTalentIdAndGroupedByStatus(talentId);
        }
        
        @Test
        void getRecommendationById_UnauthorizedUser_ReturnsUnauthorizedResponse() throws Exception {
            setupUnauthorizedMockMvc();
                    
            mockMvc.perform(get("/recommendations/{recommendationId}", "rec-id-1"))
                    .andExpect(status().isUnauthorized())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$.errors", containsString("Unauthorized access")));
            
            verify(recommendationService, never()).getById(anyString());
        }
        
        @Test
        void getRecommendationsByTalentId_UnauthorizedUser_ReturnsUnauthorizedResponse() throws Exception {
            setupUnauthorizedMockMvc();
                    
            mockMvc.perform(get("/recommendations/user/{userId}", "user-id-1"))
                    .andExpect(status().isUnauthorized())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$.errors", containsString("Unauthorized access")));
            
            verify(recommendationService, never()).getByTalentId(anyString());
        }
        
        @Test
        void getRecommendationsByTalentIdAndStatus_UnauthorizedUser_ReturnsUnauthorizedResponse() throws Exception {
            setupUnauthorizedMockMvc();
                    
            mockMvc.perform(get("/recommendations/user/{userId}/status/{status}", "user-id-1", StatusType.PENDING))
                    .andExpect(status().isUnauthorized())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$.errors", containsString("Unauthorized access")));
            
            verify(recommendationService, never()).getByTalentIdAndStatus(anyString(), org.mockito.ArgumentMatchers.any(StatusType.class));
        }
        
        @Test
        void getRecommendationById_ServiceThrowsException_ReturnsNotFoundResponse() throws Exception {
            String id = "rec-id-1";
            when(recommendationService.getById(id)).thenThrow(new RuntimeException("Some unexpected error"));
            
            mockMvc.perform(get("/recommendations/{recommendationId}", id))
                    .andExpect(status().isNotFound())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$.errors", containsString("Some unexpected error")));
    
            verify(recommendationService).getById(id);
        }
        
        @Test
        void getRecommendationsByTalentId_ServiceThrowsException_ReturnsNotFoundResponse() throws Exception {
            String talentId = "user-id-1";
            when(recommendationService.getByTalentId(talentId)).thenThrow(new RuntimeException("Permission denied"));
            
            mockMvc.perform(get("/recommendations/user/{userId}", talentId))
                    .andExpect(status().isNotFound())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$.errors", containsString("Permission denied")));
    
            verify(recommendationService).getByTalentId(talentId);
        }
        
        @Test
        void getRecommendationsByTalentIdAndStatus_ServiceThrowsException_ReturnsNotFoundResponse() throws Exception {
            String talentId = "user-id-1";
            StatusType status = StatusType.PENDING;
            when(recommendationService.getByTalentIdAndStatus(talentId, status))
                    .thenThrow(new RuntimeException("Error retrieving recommendations"));
            
            mockMvc.perform(get("/recommendations/user/{userId}/status/{status}", talentId, status))
                    .andExpect(status().isNotFound())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$.errors", containsString("Error retrieving recommendations")));
    
            verify(recommendationService).getByTalentIdAndStatus(talentId, status);
        }
        
        @Test
        void getRecommendationsByTalentIdGroupedByStatus_ServiceThrowsException_ReturnsNotFoundResponse() throws Exception {
            String talentId = "user-id-1";
            when(recommendationService.getByTalentIdAndGroupedByStatus(talentId))
                    .thenThrow(new RuntimeException("Error grouping recommendations"));
            
            mockMvc.perform(get("/recommendations/user/{userId}/grouped-by-status", talentId))
                    .andExpect(status().isNotFound())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$.errors", containsString("Error grouping recommendations")));
    
            verify(recommendationService).getByTalentIdAndGroupedByStatus(talentId);
        }
        
        @Test
        void getRecommendationsByTalentIdGroupedByStatus_UnauthorizedUser_ReturnsUnauthorizedResponse() throws Exception {
            setupUnauthorizedMockMvc();
                    
            mockMvc.perform(get("/recommendations/user/{userId}/grouped-by-status", "user-id-1"))
                    .andExpect(status().isUnauthorized())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$.errors", containsString("Unauthorized access")));
            
            verify(recommendationService, never()).getByTalentIdAndGroupedByStatus(anyString());
        }
    }
}
