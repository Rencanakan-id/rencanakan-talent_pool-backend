package rencanakan.id.talentpool.controller;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import rencanakan.id.talentpool.dto.*;
import rencanakan.id.talentpool.enums.StatusType;
import rencanakan.id.talentpool.model.User;
import rencanakan.id.talentpool.service.ExperienceServiceImpl;
import rencanakan.id.talentpool.service.RecommendationService;

@RestController
@RequestMapping("/recommendations")
public class RecommendationController {
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

}
