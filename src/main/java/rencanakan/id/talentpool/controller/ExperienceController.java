package rencanakan.id.talentpool.controller;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import rencanakan.id.talentpool.dto.EditExperienceRequestDTO;
import rencanakan.id.talentpool.dto.ExperienceResponseDTO;
import rencanakan.id.talentpool.dto.WebResponse;
import rencanakan.id.talentpool.service.ExperienceService;

@RestController
@RequestMapping("/api/experiences")
public class ExperienceController {
    @Autowired
    ExperienceService experienceService;

    @PutMapping("/{id}")
    public WebResponse<ExperienceResponseDTO> editExperienceById(@PathVariable Long id, @RequestHeader("Authorization") String token,   @Valid @RequestBody EditExperienceRequestDTO dto) {
        ExperienceResponseDTO resp= experienceService.editById(id, dto);
        return WebResponse.<ExperienceResponseDTO>builder().data(resp).build();
    }


}
