package rencanakan.id.talentpool.controller;


import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import rencanakan.id.talentpool.dto.*;
import rencanakan.id.talentpool.service.RecommendationServiceImpl;


@RestController
@RequestMapping("/recommendations")
public class RecommendationController {
    private final RecommendationServiceImpl recommendationService;

    public RecommendationController(RecommendationServiceImpl recommendationService) {
        this.recommendationService = recommendationService;
    }


    @PutMapping("/{id}")
    public ResponseEntity<WebResponse<RecommendationResponseDTO>> editRecommendationById(
            @PathVariable Long id,
            @RequestBody @Valid RecommendationRequestDTO dto) {

        RecommendationResponseDTO updatedRecommendation = recommendationService.editById(id, dto);

        return ResponseEntity.ok(WebResponse.<RecommendationResponseDTO>builder()
                .data(updatedRecommendation)
                .build());
    }
}
