package rencanakan.id.talentpool.controller;

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
import rencanakan.id.talentpool.dto.RecommendationResponseDTO;
import rencanakan.id.talentpool.enums.StatusType;
import rencanakan.id.talentpool.service.RecommendationService;

import java.util.*;

import static org.hamcrest.Matchers.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
public class RecommendationControllerTest {

    private MockMvc mockMvc;

    @Mock
    private RecommendationService recommendationService;

    @InjectMocks
    private RecommendationController recommendationController;

    private RecommendationResponseDTO recommendation1;
    private RecommendationResponseDTO recommendation2;
    private RecommendationResponseDTO recommendation3;
    private List<RecommendationResponseDTO> allRecommendations;
    private Map<StatusType, List<RecommendationResponseDTO>> groupedRecommendations;
    private String authToken;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders
                .standaloneSetup(recommendationController)
                .build();
        authToken = "Bearer mock-jwt-token";
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

        allRecommendations = Arrays.asList(recommendation1, recommendation2, recommendation3);

        groupedRecommendations = new HashMap<>();
        groupedRecommendations.put(StatusType.PENDING, Collections.singletonList(recommendation1));
        groupedRecommendations.put(StatusType.ACCEPTED, Collections.singletonList(recommendation2));
        groupedRecommendations.put(StatusType.DECLINED, Collections.singletonList(recommendation3));
    }

    @Nested
    class ReadRecommendationTests {
        
        @Test
        void getRecommendationById_ExistingId_ReturnsRecommendation() throws Exception {
            String id = "rec-id-1";
            when(recommendationService.getById(id)).thenReturn(recommendation1);
            
            mockMvc.perform(get("/recommendations/{id}", id)
                            .header("Authorization", authToken))
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
        void getAllRecommendations_ReturnsAllRecommendations() throws Exception {
            when(recommendationService.getAll()).thenReturn(allRecommendations);
            
            mockMvc.perform(get("/recommendations")
                            .header("Authorization", authToken))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$.data", hasSize(3)))
                    .andExpect(jsonPath("$.data[0].id", is(recommendation1.getId())))
                    .andExpect(jsonPath("$.data[1].id", is(recommendation2.getId())))
                    .andExpect(jsonPath("$.data[2].id", is(recommendation3.getId())));
    
            verify(recommendationService).getAll();
        }
    
        @Test
        void getRecommendationsByStatus_ExistingStatus_ReturnsFilteredRecommendations() throws Exception {
            StatusType status = StatusType.PENDING;
            when(recommendationService.getByStatus(status)).thenReturn(Collections.singletonList(recommendation1));
            
            mockMvc.perform(get("/recommendations/by-status/{status}", status)
                            .header("Authorization", authToken))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$.data", hasSize(1)))
                    .andExpect(jsonPath("$.data[0].id", is(recommendation1.getId())))
                    .andExpect(jsonPath("$.data[0].status", is(status.toString())));
    
            verify(recommendationService).getByStatus(status);
        }
    
        @Test
        void getRecommendationsGroupedByStatus_ReturnsGroupedRecommendations() throws Exception {
            when(recommendationService.getAllGroupedByStatus()).thenReturn(groupedRecommendations);
            
            mockMvc.perform(get("/recommendations/grouped-by-status")
                            .header("Authorization", authToken))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$.data." + StatusType.PENDING, hasSize(1)))
                    .andExpect(jsonPath("$.data." + StatusType.ACCEPTED, hasSize(1)))
                    .andExpect(jsonPath("$.data." + StatusType.DECLINED, hasSize(1)))
                    .andExpect(jsonPath("$.data." + StatusType.PENDING + "[0].id", is(recommendation1.getId())))
                    .andExpect(jsonPath("$.data." + StatusType.ACCEPTED + "[0].id", is(recommendation2.getId())))
                    .andExpect(jsonPath("$.data." + StatusType.DECLINED + "[0].id", is(recommendation3.getId())));
    
            verify(recommendationService).getAllGroupedByStatus();
        }
    
        @Test
        void getRecommendationsByTalentId_ExistingTalentId_ReturnsFilteredRecommendations() throws Exception {
            String talentId = "user-id-1";
            List<RecommendationResponseDTO> talent1Recommendations = Arrays.asList(recommendation1, recommendation2);
            when(recommendationService.getByTalentId(talentId)).thenReturn(talent1Recommendations);
            
            mockMvc.perform(get("/recommendations/talent/{talentId}", talentId)
                            .header("Authorization", authToken))
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
            
            mockMvc.perform(get("/recommendations/talent/{talentId}/status/{status}", talentId, status)
                            .header("Authorization", authToken))
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
            Map<StatusType, List<RecommendationResponseDTO>> groupedByTalent = new HashMap<>();
            groupedByTalent.put(StatusType.PENDING, Collections.singletonList(recommendation1));
            groupedByTalent.put(StatusType.ACCEPTED, Collections.singletonList(recommendation2));
            groupedByTalent.put(StatusType.DECLINED, Collections.emptyList());
            
            when(recommendationService.getByTalentIdAndGroupedByStatus(talentId)).thenReturn(groupedByTalent);
            
            mockMvc.perform(get("/recommendations/talent/{talentId}/grouped-by-status", talentId)
                            .header("Authorization", authToken))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$.data." + StatusType.PENDING, hasSize(1)))
                    .andExpect(jsonPath("$.data." + StatusType.ACCEPTED, hasSize(1)))
                    .andExpect(jsonPath("$.data." + StatusType.DECLINED, hasSize(0)))
                    .andExpect(jsonPath("$.data." + StatusType.PENDING + "[0].id", is(recommendation1.getId())))
                    .andExpect(jsonPath("$.data." + StatusType.ACCEPTED + "[0].id", is(recommendation2.getId())));
    
            verify(recommendationService).getByTalentIdAndGroupedByStatus(talentId);
        }
    
        @Test
        void missingAuthorizationHeader_Returns400() throws Exception { 
            mockMvc.perform(get("/recommendations"))
                    .andExpect(status().isBadRequest());
        }

        @Test
        void getRecommendationById_NonExistentId_ReturnsNotFound() throws Exception {
            String nonExistentId = "non-existent-id";
            when(recommendationService.getById(nonExistentId)).thenReturn(null);
            
            mockMvc.perform(get("/recommendations/{id}", nonExistentId)
                            .header("Authorization", authToken))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$.data").doesNotExist());
    
            verify(recommendationService).getById(nonExistentId);
        }
        
        @Test
        void getAllRecommendations_EmptyList_ReturnsEmptyArray() throws Exception {
            List<RecommendationResponseDTO> emptyList = Collections.emptyList();
            when(recommendationService.getAll()).thenReturn(emptyList);
            
            mockMvc.perform(get("/recommendations")
                            .header("Authorization", authToken))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$.data", hasSize(0)));
    
            verify(recommendationService).getAll();
        }
        
        @Test
        void getRecommendationsByStatus_NoMatchingStatus_ReturnsEmptyArray() throws Exception {
            StatusType status = StatusType.DECLINED;
            when(recommendationService.getByStatus(status)).thenReturn(Collections.emptyList());
            
            mockMvc.perform(get("/recommendations/by-status/{status}", status)
                            .header("Authorization", authToken))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$.data", hasSize(0)));
    
            verify(recommendationService).getByStatus(status);
        }
        
        @Test
        void getRecommendationsByTalentId_NonExistentTalentId_ReturnsEmptyArray() throws Exception {
            String nonExistentTalentId = "non-existent-talent";
            when(recommendationService.getByTalentId(nonExistentTalentId)).thenReturn(Collections.emptyList());
            
            mockMvc.perform(get("/recommendations/talent/{talentId}", nonExistentTalentId)
                            .header("Authorization", authToken))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$.data", hasSize(0)));
    
            verify(recommendationService).getByTalentId(nonExistentTalentId);
        }
        
        @Test
        void getRecommendationsByTalentIdAndStatus_NoMatch_ReturnsEmptyArray() throws Exception {
            String talentId = "user-id-1";
            StatusType status = StatusType.DECLINED;
            when(recommendationService.getByTalentIdAndStatus(talentId, status))
                    .thenReturn(Collections.emptyList());
            
            mockMvc.perform(get("/recommendations/talent/{talentId}/status/{status}", talentId, status)
                            .header("Authorization", authToken))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$.data", hasSize(0)));
    
            verify(recommendationService).getByTalentIdAndStatus(talentId, status);
        }
        
        @Test
        void getRecommendationsGroupedByStatus_EmptyResults_ReturnsEmptyMaps() throws Exception {
            Map<StatusType, List<RecommendationResponseDTO>> emptyGroupedRecommendations = new HashMap<>();
            emptyGroupedRecommendations.put(StatusType.PENDING, Collections.emptyList());
            emptyGroupedRecommendations.put(StatusType.ACCEPTED, Collections.emptyList());
            emptyGroupedRecommendations.put(StatusType.DECLINED, Collections.emptyList());
            
            when(recommendationService.getAllGroupedByStatus()).thenReturn(emptyGroupedRecommendations);
            
            mockMvc.perform(get("/recommendations/grouped-by-status")
                            .header("Authorization", authToken))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$.data." + StatusType.PENDING, hasSize(0)))
                    .andExpect(jsonPath("$.data." + StatusType.ACCEPTED, hasSize(0)))
                    .andExpect(jsonPath("$.data." + StatusType.DECLINED, hasSize(0)));
    
            verify(recommendationService).getAllGroupedByStatus();
        }
        
        @Test
        void getRecommendationsByTalentIdGroupedByStatus_NoRecommendations_ReturnsEmptyGroups() throws Exception {
            String talentId = "user-id-with-no-recommendations";
            Map<StatusType, List<RecommendationResponseDTO>> emptyGroupedByTalent = new HashMap<>();
            emptyGroupedByTalent.put(StatusType.PENDING, Collections.emptyList());
            emptyGroupedByTalent.put(StatusType.ACCEPTED, Collections.emptyList());
            emptyGroupedByTalent.put(StatusType.DECLINED, Collections.emptyList());
            
            when(recommendationService.getByTalentIdAndGroupedByStatus(talentId)).thenReturn(emptyGroupedByTalent);
            
            mockMvc.perform(get("/recommendations/talent/{talentId}/grouped-by-status", talentId)
                            .header("Authorization", authToken))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$.data." + StatusType.PENDING, hasSize(0)))
                    .andExpect(jsonPath("$.data." + StatusType.ACCEPTED, hasSize(0)))
                    .andExpect(jsonPath("$.data." + StatusType.DECLINED, hasSize(0)));
    
            verify(recommendationService).getByTalentIdAndGroupedByStatus(talentId);
        }
    }
}
