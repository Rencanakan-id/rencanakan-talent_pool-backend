package rencanakan.id.talentpool.controller;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import rencanakan.id.talentpool.dto.ExperienceRequestDTO;
import rencanakan.id.talentpool.dto.ExperienceResponseDTO;
import rencanakan.id.talentpool.dto.WebResponse;
import rencanakan.id.talentpool.model.User;
import rencanakan.id.talentpool.service.ExperienceService;

import java.util.List;

@RestController
@RequestMapping("/experiences")
public class ExperienceController {

    private final ExperienceService experienceService;

    public ExperienceController(ExperienceService experienceService) {
        this.experienceService = experienceService;
    }

    @GetMapping("/user/contractor/{talent_id}")
    public ResponseEntity<WebResponse<List<ExperienceResponseDTO>>> getExperiencesTalentFromContractorById(
            @PathVariable("talent_id") String talentId) {

        List<ExperienceResponseDTO> resp = experienceService.getByTalentId(talentId);
        if (resp == null) {
            return ResponseEntity.status(404).body(WebResponse.<List<ExperienceResponseDTO>>builder()
                    .errors("Experience not found.")
                    .build());
        }
        return ResponseEntity.ok(WebResponse.<List<ExperienceResponseDTO>>builder()
                .data(resp)
                .build());
    }
    @GetMapping("/user/{talent_id}")
    public ResponseEntity<WebResponse<List<ExperienceResponseDTO>>> getExperiencesByTalentId(
            @PathVariable("talent_id") String talentId,
            @AuthenticationPrincipal User user) {

        System.out.println("masuk sini kok");

        if (user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(WebResponse.<List<ExperienceResponseDTO>>builder()
                            .errors("Unauthorized access")
                            .build());
        }

        try {
            List<ExperienceResponseDTO> resp = experienceService.getByTalentId(talentId);
            return ResponseEntity.ok(WebResponse.<List<ExperienceResponseDTO>>builder()
                    .data(resp)
                    .build());
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(WebResponse.<List<ExperienceResponseDTO>>builder()
                            .errors(e.getMessage())
                            .build());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(WebResponse.<List<ExperienceResponseDTO>>builder()
                            .errors(e.getMessage())
                            .build());
        }
    }

    @PostMapping
    public ResponseEntity<WebResponse<ExperienceResponseDTO>> createExperience(
            @RequestBody @Valid ExperienceRequestDTO request,
            @AuthenticationPrincipal User user
    ) {
        ExperienceResponseDTO createdExperience = experienceService.createExperience(user.getId(), request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(WebResponse.<ExperienceResponseDTO>builder()
                        .data(createdExperience)
                        .build());
    }

    @PutMapping("/{id}")
    public ResponseEntity<WebResponse<ExperienceResponseDTO>> editExperienceById(
            @AuthenticationPrincipal User user,
            @PathVariable("id") Long id,
            @RequestBody @Valid ExperienceRequestDTO dto) {

        ExperienceResponseDTO updatedExperience = experienceService.editById(user.getId(), id, dto);

        return ResponseEntity.ok(WebResponse.<ExperienceResponseDTO>builder()
                .data(updatedExperience)
                .build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<WebResponse<String>> deleteExperienceById(
            @PathVariable("id") Long id,
            @AuthenticationPrincipal User user) {

        if (user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(WebResponse.<String>builder()
                    .errors("Unauthorized access")
                    .build());
        }
        try {
            experienceService.deleteById(id);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(WebResponse.<String>builder()
                    .errors(e.getMessage())
                    .build());
        }

        return ResponseEntity.ok(WebResponse.<String>builder()
                .data("Experience with id " + id + " deleted")
                .build());
    }
}