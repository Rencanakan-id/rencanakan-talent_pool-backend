package rencanakan.id.talentpool.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
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
            @AuthenticationPrincipal User user) {

        if (user == null) {
            return ResponseEntity.badRequest().build();
        }

        RecommendationResponseDTO response = recommendationService.createRecommendation(user.getId(), request);
        return ResponseEntity.ok(response);
    }
}
