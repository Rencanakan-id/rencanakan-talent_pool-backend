package rencanakan.id.talentpool.controller;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import rencanakan.id.talentpool.dto.RecommendationRequestDTO;import org.springframework.web.server.ResponseStatusException;
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
            @PathVariable String id,
            @RequestBody StatusType status,
            @AuthenticationPrincipal User user){

        RecommendationResponseDTO res = this.recommendationService.editStatusById(user.getId(), id, status);
        WebResponse<RecommendationResponseDTO> response = WebResponse.<RecommendationResponseDTO>builder()
                .data(res)
                .build();

        return ResponseEntity.ok(response);

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

    @PostMapping
    public ResponseEntity<RecommendationResponseDTO> createRecommendation(
            @RequestBody @Valid RecommendationRequestDTO request,
            @AuthenticationPrincipal User user) {

        if (user == null) {
            return ResponseEntity.badRequest().build();
        }

        RecommendationResponseDTO response = recommendationService.createRecommendation(user.getId(), request);
        return ResponseEntity.ok(response);
        }

    }
