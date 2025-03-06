package rencanakan.id.talentpool.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import rencanakan.id.talentpool.dto.ExperienceRequestDTO;
import rencanakan.id.talentpool.model.Experience;
import rencanakan.id.talentpool.service.ExperienceServiceImpl;

@RestController
@RequestMapping("/experience")
public class ExperienceController {
    private final ExperienceServiceImpl experienceService;

    public ExperienceController(ExperienceServiceImpl experienceService) {
        this.experienceService = experienceService;
    }

    @PostMapping()
    public ResponseEntity<Experience> createExperience(@RequestBody ExperienceRequestDTO request) {
        try {
            Experience createdExperience = experienceService.createExperience(request);
            return ResponseEntity.ok(createdExperience);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(null);
        }
    }
}