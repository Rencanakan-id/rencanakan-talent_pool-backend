package rencanakan.id.talentpool.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import rencanakan.id.talentpool.dto.RecommendationRequestDTO;
import rencanakan.id.talentpool.dto.RecommendationResponseDTO;
import rencanakan.id.talentpool.model.User;
import rencanakan.id.talentpool.service.RecommendationServiceImpl;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/recommendations")
@RequiredArgsConstructor
public class RecommendationController {

    private final RecommendationServiceImpl recommendationService;

    @PostMapping
    public ResponseEntity<RecommendationResponseDTO> createRecommendation(
            @RequestBody @Valid RecommendationRequestDTO request,
            @RequestAttribute("currentUser") User currentUser) {
        return  null;
    }
}
