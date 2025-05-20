package rencanakan.id.talentpool.controller;

import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import rencanakan.id.talentpool.dto.RecommendationRequestDTO;
import org.springframework.security.access.AccessDeniedException;
import rencanakan.id.talentpool.model.User;
import rencanakan.id.talentpool.dto.*;
import rencanakan.id.talentpool.enums.StatusType;
import rencanakan.id.talentpool.service.RecommendationService;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/recommendations")
public class RecommendationController {

    private static final String UNAUTHORIZED_ACCESS_MESSAGE = "Unauthorized access";
    private static final String NO_RECOMMENDATIONS_FOR_USER = "No recommendations found for user with id: ";

    private final RecommendationService recommendationService;

    public RecommendationController(RecommendationService recommendationService) {
        this.recommendationService = recommendationService;
    }

    @PatchMapping("/{id}")
    public ResponseEntity<WebResponse<RecommendationResponseDTO>> editStatusById(
            @PathVariable("id") String id,
            @RequestBody StatusType status,
            @AuthenticationPrincipal User user){

        RecommendationResponseDTO res = this.recommendationService.editStatusById(user.getId(), id, status);
        WebResponse<RecommendationResponseDTO> response = WebResponse.<RecommendationResponseDTO>builder()
                .data(res)
                .build();

        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{recommendationId}/contractor/{contractorId}")
    public ResponseEntity<WebResponse<RecommendationResponseDTO>> deleteByStatusId(
            @PathVariable("recommendationId") String recommendationId,
            @PathVariable("contractorId") Long contractorId){
        RecommendationResponseDTO res = this.recommendationService
                .deleteByIdContractor(contractorId, recommendationId);
        WebResponse<RecommendationResponseDTO> response = WebResponse.<RecommendationResponseDTO>builder()
                .data(res)
                .build();
        return ResponseEntity.ok(response);
    }
      
    @DeleteMapping("/{recommendationId}")
    public ResponseEntity<WebResponse<RecommendationResponseDTO>> deleteByStatusId(
            @PathVariable("recommendationId") String recommendationId,
            @AuthenticationPrincipal User user ){
        RecommendationResponseDTO res = this.recommendationService.deleteByIdTalent(user.getId(),  recommendationId);
        WebResponse<RecommendationResponseDTO> response = WebResponse.<RecommendationResponseDTO>builder()
                .data(res)
                .build();
        return ResponseEntity.ok(response);
    }

    @PatchMapping("/{recommendationId}/accept")
    public ResponseEntity<WebResponse<RecommendationResponseDTO>> acceptById(
            @PathVariable("recommendationId") String recommendationId,
            @AuthenticationPrincipal User user) {

        try {
            RecommendationResponseDTO resp = recommendationService.editStatusById(user.getId(), recommendationId, StatusType.ACCEPTED);

            return ResponseEntity.ok(WebResponse.<RecommendationResponseDTO>builder()
                        .data(resp)
                        .build());
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                    WebResponse.<RecommendationResponseDTO>builder()
                        .errors(e.getMessage())
                        .build());
        } catch (AccessDeniedException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(
                    WebResponse.<RecommendationResponseDTO>builder()
                        .errors(e.getMessage())
                        .build());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                    WebResponse.<RecommendationResponseDTO>builder()
                        .errors(e.getMessage())
                        .build());
        }
    }

    @PatchMapping("/{recommendationId}/reject")
    public ResponseEntity<WebResponse<RecommendationResponseDTO>> rejectById(
            @PathVariable("recommendationId") String recommendationId,
            @AuthenticationPrincipal User user) {

        try {
            RecommendationResponseDTO resp = recommendationService.editStatusById(user.getId(), recommendationId, StatusType.DECLINED);

            return ResponseEntity.ok(WebResponse.<RecommendationResponseDTO>builder()
                        .data(resp)
                        .build());
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                    WebResponse.<RecommendationResponseDTO>builder()
                        .errors(e.getMessage())
                        .build());
        } catch (AccessDeniedException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(
                    WebResponse.<RecommendationResponseDTO>builder()
                        .errors(e.getMessage())
                        .build());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                    WebResponse.<RecommendationResponseDTO>builder()
                        .errors(e.getMessage())
                        .build());
        }
    }

    @GetMapping("/{recommendationId}")
    public ResponseEntity<WebResponse<RecommendationResponseDTO>> getRecommendationById(
            @PathVariable("recommendationId") String recommendationId,
            @AuthenticationPrincipal User user) {

        if (user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(
                    WebResponse.<RecommendationResponseDTO>builder()
                        .errors(UNAUTHORIZED_ACCESS_MESSAGE)
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
            @PathVariable("userId") String userId,
            @AuthenticationPrincipal User user) {

        if (user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(
                    WebResponse.<List<RecommendationResponseDTO>>builder()
                        .errors(UNAUTHORIZED_ACCESS_MESSAGE)
                        .build());
        }

        try {
            List<RecommendationResponseDTO> recommendations = recommendationService.getByTalentId(userId);

            if (recommendations.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                        WebResponse.<List<RecommendationResponseDTO>>builder()
                            .errors(NO_RECOMMENDATIONS_FOR_USER + userId)
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
            @PathVariable("userId") String userId,
            @PathVariable("status") StatusType status,
            @AuthenticationPrincipal User user) {

        if (user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(
                    WebResponse.<List<RecommendationResponseDTO>>builder()
                        .errors(UNAUTHORIZED_ACCESS_MESSAGE)
                        .build());
        }

        try {
            List<RecommendationResponseDTO> recommendations = recommendationService.getByTalentIdAndStatus(userId, status);

            if (recommendations.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                        WebResponse.<List<RecommendationResponseDTO>>builder()
                            .errors(NO_RECOMMENDATIONS_FOR_USER + userId + " and status: " + status)
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
                        .errors(UNAUTHORIZED_ACCESS_MESSAGE)
                        .build());
        }

        try {
            Map<StatusType, List<RecommendationResponseDTO>> groupedRecommendations = recommendationService.getByTalentIdAndGroupedByStatus(userId);

            if (groupedRecommendations.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                        WebResponse.<Map<StatusType, List<RecommendationResponseDTO>>>builder()
                            .errors(NO_RECOMMENDATIONS_FOR_USER + userId)
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

    @PostMapping("/user/contractor/{talentId}")
    public ResponseEntity<WebResponse<RecommendationResponseDTO>> createRecommendation(
            @PathVariable("talentId") String talentId,
            @RequestBody @Valid RecommendationRequestDTO request) {
        try {
            RecommendationResponseDTO response = recommendationService.createRecommendation(talentId, request);
            return ResponseEntity.ok(WebResponse.<RecommendationResponseDTO>builder()
                    .data(response)
                    .build());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                    WebResponse.<RecommendationResponseDTO>builder()
                            .errors(e.getMessage())
                            .build());
        }
    }

    @GetMapping("/user/contractor/{userId}")
    public ResponseEntity<WebResponse<List<RecommendationResponseDTO>>> getRecommendationTalentFromContractorById(
            @PathVariable("userId") String userId) {

        List<RecommendationResponseDTO> recommendations = recommendationService.getByTalentId(userId);

        if (recommendations.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                    WebResponse.<List<RecommendationResponseDTO>>builder()
                        .errors(NO_RECOMMENDATIONS_FOR_USER + userId)
                        .build());
        }

        return ResponseEntity.ok(WebResponse.<List<RecommendationResponseDTO>>builder()
                    .data(recommendations)
                    .build());
    }

    @PutMapping("/{recommendationId}/contractor/{contractorId}")
    public ResponseEntity<WebResponse<RecommendationResponseDTO>> editRecommendationById(
            @PathVariable("recommendationId") String recommendationId,
            @PathVariable("contractorId") Long contractorId,
            @RequestBody @Valid RecommendationRequestDTO editRequest) {

        try {
            RecommendationResponseDTO resp = recommendationService.editById(contractorId, recommendationId, editRequest);

            return ResponseEntity.ok(WebResponse.<RecommendationResponseDTO>builder()
                    .data(resp)
                    .build());
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                    WebResponse.<RecommendationResponseDTO>builder()
                            .errors(e.getMessage())
                            .build());
        } catch (AccessDeniedException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(
                    WebResponse.<RecommendationResponseDTO>builder()
                            .errors(e.getMessage())
                            .build());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                    WebResponse.<RecommendationResponseDTO>builder()
                            .errors(e.getMessage())
                            .build());
        }
    }
}
