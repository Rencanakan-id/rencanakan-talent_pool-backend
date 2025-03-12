package rencanakan.id.talentpool.service;

import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import rencanakan.id.talentpool.dto.EditExperienceRequestDTO;
import rencanakan.id.talentpool.dto.ExperienceListResponseDTO;
import rencanakan.id.talentpool.dto.ExperienceResponseDTO;
import rencanakan.id.talentpool.model.Experience;
import rencanakan.id.talentpool.repository.ExperienceRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ExperienceServiceImpl implements ExperienceService {
    @Autowired
    ExperienceRepository experienceRepository;

    @Override
    public ExperienceListResponseDTO getByTalentId(Long id) {
        List<Experience> experiences = experienceRepository.findByTalentId(id);
        if (experiences.isEmpty()) {
            throw new EntityNotFoundException("Experience is empty");
        } else {
            List<ExperienceResponseDTO> response = experiences.stream().map(this::toExperienceResponseDTO).collect(Collectors.toList());
            return new ExperienceListResponseDTO(response);
        }

    }

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

        Experience new_exp = experienceRepository.save(experience);
        return toExperienceResponseDTO(new_exp);
    }

    @Override
    public void deleteById(Long id) {
        Experience experience = experienceRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Experience by id " + id + " not found"));
        experienceRepository.delete(experience);
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
