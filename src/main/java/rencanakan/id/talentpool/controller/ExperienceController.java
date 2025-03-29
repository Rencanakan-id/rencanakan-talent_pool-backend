package rencanakan.id.talentpool.controller;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import rencanakan.id.talentpool.dto.ExperienceListResponseDTO;
import rencanakan.id.talentpool.dto.ExperienceRequestDTO;
import rencanakan.id.talentpool.dto.ExperienceResponseDTO;
import rencanakan.id.talentpool.dto.WebResponse;
import rencanakan.id.talentpool.model.User;
import rencanakan.id.talentpool.service.ExperienceService;
import rencanakan.id.talentpool.service.ExperienceServiceImpl;

import java.util.List;

@RestController
@RequestMapping("/experiences")
public class ExperienceController {

    private final ExperienceServiceImpl experienceService;

    public ExperienceController(ExperienceServiceImpl experienceService) {
        this.experienceService = experienceService;
    }

    @GetMapping("/{talent_id}")
    public ResponseEntity<WebResponse<List<ExperienceResponseDTO>>> getExperiencesByTalentId(
            @PathVariable("talent_id") String talentId,
            @RequestHeader("Authorization") String token) {

        List<ExperienceResponseDTO> resp = experienceService.getByTalentId(talentId);
        return ResponseEntity.ok(WebResponse.<List<ExperienceResponseDTO>>builder()
                .data(resp)
                .build());
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

    @GetMapping
    public ResponseEntity<Object> createExperiences(
    ) {
        System.out.println(1111111111);
        return ResponseEntity.status(HttpStatus.OK).body(null);
    }

    @PutMapping("/{id}")
    public ResponseEntity<WebResponse<ExperienceResponseDTO>> editExperienceById(
            @PathVariable Long id,
            @RequestHeader("Authorization") String token,
            @RequestBody @Valid ExperienceRequestDTO dto) {

        ExperienceResponseDTO updatedExperience = experienceService.editById(id, dto);

        return ResponseEntity.ok(WebResponse.<ExperienceResponseDTO>builder()
                .data(updatedExperience)
                .build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<WebResponse<String>> deleteExperienceById(
            @PathVariable("id") Long id,
            @RequestHeader("Authorization") String token) {

        experienceService.deleteById(id);

        return ResponseEntity.ok(WebResponse.<String>builder()
                .data("Experience with id " + id + " deleted")
                .build());
    }
}