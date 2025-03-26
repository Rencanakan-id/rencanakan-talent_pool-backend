package rencanakan.id.talentpool.controller;

import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import rencanakan.id.talentpool.dto.*;
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
            @RequestBody @Valid RecommendationStatusRequestDTO dto){
        RecommendationResponseDTO res = this.recommendationService.editStatusById(id, dto);
        WebResponse<RecommendationResponseDTO> response = WebResponse.<RecommendationResponseDTO>builder()
                .data(res)
                .build();

        return ResponseEntity.ok(response);

    }
    @DeleteMapping("/{id}")
    public ResponseEntity<WebResponse<RecommendationResponseDTO>> deleteByStatusId(
            @PathVariable String id){
        RecommendationResponseDTO res = this.recommendationService.deleteById(id);
        WebResponse<RecommendationResponseDTO> response = WebResponse.<RecommendationResponseDTO>builder()
                .data(res)
                .build();
        return ResponseEntity.ok(response);

    }

}
