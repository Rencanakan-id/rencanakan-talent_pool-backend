package rencanakan.id.talentPool.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import rencanakan.id.talentPool.dto.ExperienceDTO;
import rencanakan.id.talentPool.model.Experience;
import rencanakan.id.talentPool.model.Talent;
import rencanakan.id.talentPool.repository.ExperienceRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ExperienceService implements ExperienceServiceInterface {

    @Autowired
    private ExperienceRepository experienceRepository;

    // Convert Experience to ExperienceDTO
    private ExperienceDTO convertToDTO(Experience experience) {
        return new ExperienceDTO(
                experience.getId(),
                experience.getTitle(),
                experience.getCompany(),
                experience.getStartDate(),
                experience.getEndDate(),
                experience.getPhotoUrl(),
                experience.getTalent().getId()
        );
    }

    // Convert ExperienceDTO to Experience
    private Experience convertToEntity(ExperienceDTO experienceDTO) {
        Experience experience = new Experience();
        experience.setTitle(experienceDTO.getTitle());
        experience.setCompany(experienceDTO.getCompany());
        experience.setStartDate(experienceDTO.getStartDate());
        experience.setEndDate(experienceDTO.getEndDate());
        experience.setPhotoUrl(experienceDTO.getPhotoUrl());

        Talent talent = new Talent();
        talent.setId(experienceDTO.getTalentId());
        experience.setTalent(talent);

        return experience;
    }

    // Get experiences by Talent ID and return as DTO
    public List<ExperienceDTO> getExperiencesByTalentId(String talentId) {
        List<Experience> experiences = experienceRepository.findByTalentId(talentId);
        return experiences.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    // Add a new experience, receive and return as DTO
    public ExperienceDTO addExperience(Experience experience) {
        Experience savedExperience = experienceRepository.save(experience);
        return convertToDTO(savedExperience);
    }

}
