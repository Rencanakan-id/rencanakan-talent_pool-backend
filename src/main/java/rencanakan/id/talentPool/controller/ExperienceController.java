package rencanakan.id.talentPool.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import rencanakan.id.talentPool.dto.ExperienceDTO;
import rencanakan.id.talentPool.model.Experience;
import rencanakan.id.talentPool.service.ExperienceService;

import rencanakan.id.talentPool.model.Talent;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/experiences")
public class ExperienceController {

    @Autowired
    private ExperienceService experienceService;

    // Endpoint untuk mendapatkan pengalaman berdasarkan Talent ID
    @GetMapping("/by-talent/{talentId}")
    public List<ExperienceDTO> getExperiencesByTalentId(@PathVariable String talentId) {
        List<ExperienceDTO> experiences = experienceService.getExperiencesByTalentId(talentId);
        return experiences.stream()
                .map(experience -> new ExperienceDTO(
                        experience.getId(),
                        experience.getTitle(),
                        experience.getCompany(),
                        experience.getStartDate(),
                        experience.getEndDate(),
                        experience.getPhotoUrl(),
                        experience.getTalentId()))  // Assuming talent has getId method
                .collect(Collectors.toList());
    }

    // Endpoint untuk menambahkan pengalaman
    @PostMapping("/add")
    public ExperienceDTO addExperience(@RequestBody ExperienceDTO experienceDTO) {
        Experience experience = new Experience();
        experience.setTitle(experienceDTO.getTitle());
        experience.setCompany(experienceDTO.getCompany());
        experience.setStartDate(experienceDTO.getStartDate());
        experience.setEndDate(experienceDTO.getEndDate());
        experience.setPhotoUrl(experienceDTO.getPhotoUrl());

        // Assuming Talent ID is sent, and we fetch Talent object based on that
        Talent talent = new Talent();  // Get Talent by ID, assume appropriate service
        talent.setId(experienceDTO.getTalentId());
        experience.setTalent(talent);

        return experienceService.addExperience(experience);
    }
}
