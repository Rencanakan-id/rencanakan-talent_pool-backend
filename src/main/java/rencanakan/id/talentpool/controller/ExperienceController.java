package rencanakan.id.talentpool.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;
import rencanakan.id.talentpool.dto.ExperienceRequestDTO;
import rencanakan.id.talentpool.service.ExperienceServiceImpl;
import rencanakan.id.talentpool.dto.ExperienceResponseDTO;
import rencanakan.id.talentpool.dto.WebResponse;

@RestController
@RequestMapping("/experiences")
public class ExperienceController {
    private final ExperienceServiceImpl experienceService;

    public ExperienceController(ExperienceServiceImpl experienceService) {
        this.experienceService = experienceService;
    }

    @PostMapping
    public ResponseEntity<WebResponse<ExperienceResponseDTO>> createExperience(@RequestBody @Valid ExperienceRequestDTO request) {
        ExperienceResponseDTO createdExperience = experienceService.createExperience(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(WebResponse.<ExperienceResponseDTO>builder()
                        .data(createdExperience)
                        .build());
    }

    @PutMapping("/{id}")
    public ResponseEntity<WebResponse<ExperienceResponseDTO>> editExperienceById(
            @PathVariable Long id,
            @RequestHeader("Authorization") String token,
            @Valid @RequestBody ExperienceRequestDTO dto) {

        ExperienceResponseDTO updatedExperience = experienceService.editById(id, dto);

        return ResponseEntity.ok(WebResponse.<ExperienceResponseDTO>builder()
                .data(updatedExperience)
                .build());
    }
}