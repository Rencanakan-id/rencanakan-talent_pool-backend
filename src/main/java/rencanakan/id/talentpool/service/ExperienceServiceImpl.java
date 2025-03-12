package rencanakan.id.talentpool.service;

import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import rencanakan.id.talentpool.dto.EditExperienceRequestDTO;
import rencanakan.id.talentpool.dto.ExperienceResponseDTO;
import rencanakan.id.talentpool.model.Experience;
import rencanakan.id.talentpool.repository.ExperienceRepository;

@Service
public class ExperienceServiceImpl implements  ExperienceService{
    @Autowired
    ExperienceRepository experienceRepository;
    @Override
    public ExperienceResponseDTO editById(Long id, EditExperienceRequestDTO dto) {
        Experience experience = experienceRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Experience not found"));

        experience.setTitle(dto.getTitle());
        experience.setCompany(dto.getCompany());
        experience.setEmploymentType(dto.getEmploymentType());
        experience.setStartDate(dto.getStartDate());
        experience.setEndDate(dto.getEndDate());
        experience.setLocation(dto.getLocation());
        experience.setLocationType(dto.getLocationType());
        experience.setTalentId(dto.getTalentId());

        Experience new_exp =  experienceRepository.save(experience);
        return toExperienceResponseDTO(new_exp);
    }

    public ExperienceResponseDTO toExperienceResponseDTO(Experience experience) {
        ExperienceResponseDTO dto = new ExperienceResponseDTO();
        dto.setId(experience.getId());
        dto.setTitle(experience.getTitle());
        dto.setCompany(experience.getCompany());
        dto.setEmploymentType(experience.getEmploymentType());
        dto.setStartDate(experience.getStartDate());
        dto.setEndDate(experience.getEndDate());
        dto.setLocation(experience.getLocation());
        dto.setLocationType(experience.getLocationType());
        dto.setTalentId(experience.getTalentId());
        return dto;
    }

}
