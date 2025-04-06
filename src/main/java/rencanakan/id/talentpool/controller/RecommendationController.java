package rencanakan.id.talentpool.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import rencanakan.id.talentpool.dto.RecommendationResponseDTO;
import rencanakan.id.talentpool.dto.WebResponse;
import rencanakan.id.talentpool.enums.StatusType;
import rencanakan.id.talentpool.model.User;
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
    
    @GetMapping("/{recommendationId}")
    public ResponseEntity<WebResponse<RecommendationResponseDTO>> getRecommendationById(
            @PathVariable("recommendationId") String recommendationId, 
            @AuthenticationPrincipal User user) {
        
        if (user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(
                    WebResponse.<RecommendationResponseDTO>builder()
                        .errors("Unauthorized access")
                        .build());
        }
        
        try {
            RecommendationResponseDTO resp = recommendationService.getById(recommendationId);

            return ResponseEntity.ok(WebResponse.<RecommendationResponseDTO>builder()
                        .data(resp)
                        .build());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                    WebResponse.<RecommendationResponseDTO>builder()
                        .errors(e.getMessage())
                        .build());
        }
    }
    
    @GetMapping("/user/{userId}")
    public ResponseEntity<WebResponse<List<RecommendationResponseDTO>>> getRecommendationsByTalentId(
            @PathVariable("userId") String talentId,
            @AuthenticationPrincipal User user) {

        if (user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(
                    WebResponse.<List<RecommendationResponseDTO>>builder()
                        .errors("Unauthorized access")
                        .build());
        }
        
        try {
            List<RecommendationResponseDTO> recommendations = recommendationService.getByTalentId(talentId);

            if (recommendations.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                        WebResponse.<List<RecommendationResponseDTO>>builder()
                            .errors("No recommendations found for user with id: " + talentId)
                            .build());
            }

            return ResponseEntity.ok(WebResponse.<List<RecommendationResponseDTO>>builder()
                        .data(recommendations)
                        .build());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                    WebResponse.<List<RecommendationResponseDTO>>builder()
                        .errors(e.getMessage())
                        .build());
        }
    }
    
    @GetMapping("/user/{userId}/status/{status}")
    public ResponseEntity<WebResponse<List<RecommendationResponseDTO>>> getRecommendationsByTalentIdAndStatus(
            @PathVariable("userId") String talentId,
            @PathVariable("status") StatusType status,
            @AuthenticationPrincipal User user) {

        if (user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(
                    WebResponse.<List<RecommendationResponseDTO>>builder()
                        .errors("Unauthorized access")
                        .build());
        }

        try {
            List<RecommendationResponseDTO> recommendations = recommendationService.getByTalentIdAndStatus(talentId, status);

            if (recommendations.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                        WebResponse.<List<RecommendationResponseDTO>>builder()
                            .errors("No recommendations found for user with id: " + talentId + " and status: " + status)
                            .build());
            }

            return ResponseEntity.ok(WebResponse.<List<RecommendationResponseDTO>>builder()
                        .data(recommendations)
                        .build());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                    WebResponse.<List<RecommendationResponseDTO>>builder()
                        .errors(e.getMessage())
                        .build());
        }
    }
    
    @GetMapping("/user/{userId}/grouped-by-status")
    public ResponseEntity<WebResponse<Map<StatusType, List<RecommendationResponseDTO>>>> getRecommendationsByTalentIdGroupedByStatus(
            @PathVariable("userId") String userId,
            @AuthenticationPrincipal User user) {
        
        if (user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(
                    WebResponse.<Map<StatusType, List<RecommendationResponseDTO>>>builder()
                        .errors("Unauthorized access")
                        .build());
        }

        try {
            Map<StatusType, List<RecommendationResponseDTO>> groupedRecommendations = recommendationService.getByTalentIdAndGroupedByStatus(userId);

            if (groupedRecommendations.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                        WebResponse.<Map<StatusType, List<RecommendationResponseDTO>>>builder()
                            .errors("No recommendations found for user with id: " + userId)
                            .build());
            }

            return ResponseEntity.ok(WebResponse.<Map<StatusType, List<RecommendationResponseDTO>>>builder()
                        .data(groupedRecommendations)
                        .build());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                    WebResponse.<Map<StatusType, List<RecommendationResponseDTO>>>builder()
                        .errors(e.getMessage())
                        .build());
        }
    }
}
