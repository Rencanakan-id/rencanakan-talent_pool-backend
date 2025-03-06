package rencanakan.id.talentPool.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import rencanakan.id.talentPool.dto.ExperienceRequestDTO;
import rencanakan.id.talentPool.model.Experience;
import rencanakan.id.talentPool.service.ExperienceServiceImpl;

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