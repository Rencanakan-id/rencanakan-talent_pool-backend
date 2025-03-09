package rencanakan.id.talentPool.controller;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import rencanakan.id.talentPool.dto.EditExperienceRequestDTO;
import rencanakan.id.talentPool.dto.ExperienceListResponseDTO;
import rencanakan.id.talentPool.dto.ExperienceResponseDTO;
import rencanakan.id.talentPool.dto.WebResponse;
import rencanakan.id.talentPool.service.ExperienceService;

@RestController
@RequestMapping("/api/experiences")
public class ExperienceController {
    @Autowired
    ExperienceService experienceService;

    @GetMapping("/{talent_id}")
    public WebResponse<ExperienceListResponseDTO> getExperiencesByTalentId(@PathVariable Long talent_id, @RequestHeader("Authorization") String token) {
        ExperienceListResponseDTO resp = experienceService.getByTalentId(talent_id);
        return WebResponse.<ExperienceListResponseDTO>builder()
                .data(resp)
                .build();
    }

    @PutMapping("/{id}")
    public WebResponse<ExperienceResponseDTO> editExperienceById(@PathVariable Long id, @RequestHeader("Authorization") String token,   @Valid @RequestBody EditExperienceRequestDTO dto) {
        ExperienceResponseDTO resp= experienceService.editById(id, dto);
        return WebResponse.<ExperienceResponseDTO>builder().data(resp).build();
    }

    @DeleteMapping("/{id}")
    public WebResponse<String> deleteExperienceById(@PathVariable("id") Long id, @RequestHeader("Authorization") String token) {
        experienceService.deleteById(id);
        return WebResponse.<String>builder()
                .data("Experience with id " + id + " deleted")
                .build();
    }
}
