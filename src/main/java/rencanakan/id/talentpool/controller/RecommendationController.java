package rencanakan.id.talentpool.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import rencanakan.id.talentpool.dto.RecommendationRequestDTO;
import rencanakan.id.talentpool.dto.RecommendationResponseDTO;
import rencanakan.id.talentpool.model.User;
import rencanakan.id.talentpool.service.RecommendationServiceImpl;

@RestController
@RequestMapping("/recommendations")
@RequiredArgsConstructor
public class RecommendationController {

    private final RecommendationServiceImpl recommendationService;

    @PostMapping
    public ResponseEntity<RecommendationResponseDTO> createRecommendation(
            @RequestBody @Valid RecommendationRequestDTO request,
            @RequestAttribute(value = "currentUser", required = false) User currentUser) {

        if (currentUser == null) {
            return ResponseEntity.badRequest().build();
        }

        RecommendationResponseDTO response = recommendationService.createRecommendation(currentUser.getId(), request);
        return ResponseEntity.ok(response);
    }
}
