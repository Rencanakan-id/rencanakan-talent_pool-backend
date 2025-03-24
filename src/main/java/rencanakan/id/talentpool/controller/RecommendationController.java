package rencanakan.id.talentpool.controller;

import org.springframework.web.bind.annotation.*;
import rencanakan.id.talentpool.dto.RecommendationResponseDTO;
import rencanakan.id.talentpool.dto.WebResponse;
import rencanakan.id.talentpool.enums.StatusType;
import rencanakan.id.talentpool.service.RecommendationService;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/recommendations")
public class RecommendationController {
    
    private final RecommendationService recommendationService;

    public RecommendationController(RecommendationService recommendationService) {
        this.recommendationService = recommendationService;
    }
    
    @GetMapping("/{id}")
    public WebResponse<RecommendationResponseDTO> getRecommendationById(
            @PathVariable String id, 
            @RequestHeader("Authorization") String token) {
        RecommendationResponseDTO resp = recommendationService.getById(id);
        return WebResponse.<RecommendationResponseDTO>builder()
                .data(resp)
                .build();
    }
    
    @GetMapping
    public WebResponse<List<RecommendationResponseDTO>> getAllRecommendations(
            @RequestHeader("Authorization") String token) {
        List<RecommendationResponseDTO> recommendations = recommendationService.getAll();
        return WebResponse.<List<RecommendationResponseDTO>>builder()
                .data(recommendations)
                .build();
    }
    
    @GetMapping("/by-status/{status}")
    public WebResponse<List<RecommendationResponseDTO>> getRecommendationsByStatus(
            @PathVariable StatusType status,
            @RequestHeader("Authorization") String token) {
        List<RecommendationResponseDTO> recommendations = recommendationService.getByStatus(status);
        return WebResponse.<List<RecommendationResponseDTO>>builder()
                .data(recommendations)
                .build();
    }
    
    @GetMapping("/grouped-by-status")
    public WebResponse<Map<StatusType, List<RecommendationResponseDTO>>> getRecommendationsGroupedByStatus(
            @RequestHeader("Authorization") String token) {
        Map<StatusType, List<RecommendationResponseDTO>> groupedRecommendations = 
                recommendationService.getAllGroupedByStatus();
        return WebResponse.<Map<StatusType, List<RecommendationResponseDTO>>>builder()
                .data(groupedRecommendations)
                .build();
    }
    
    @GetMapping("/talent/{talentId}")
    public WebResponse<List<RecommendationResponseDTO>> getRecommendationsByTalentId(
            @PathVariable String talentId,
            @RequestHeader("Authorization") String token) {
        List<RecommendationResponseDTO> recommendations = recommendationService.getByTalentId(talentId);
        return WebResponse.<List<RecommendationResponseDTO>>builder()
                .data(recommendations)
                .build();
    }
    
    @GetMapping("/talent/{talentId}/status/{status}")
    public WebResponse<List<RecommendationResponseDTO>> getRecommendationsByTalentIdAndStatus(
            @PathVariable String talentId,
            @PathVariable StatusType status,
            @RequestHeader("Authorization") String token) {
        List<RecommendationResponseDTO> recommendations = 
                recommendationService.getByTalentIdAndStatus(talentId, status);
        return WebResponse.<List<RecommendationResponseDTO>>builder()
                .data(recommendations)
                .build();
    }
    
    @GetMapping("/talent/{talentId}/grouped-by-status")
    public WebResponse<Map<StatusType, List<RecommendationResponseDTO>>> getRecommendationsByTalentIdGroupedByStatus(
            @PathVariable String talentId,
            @RequestHeader("Authorization") String token) {
        Map<StatusType, List<RecommendationResponseDTO>> groupedRecommendations = 
                recommendationService.getByTalentIdAndGroupedByStatus(talentId);
        return WebResponse.<Map<StatusType, List<RecommendationResponseDTO>>>builder()
                .data(groupedRecommendations)
                .build();
    }
}
