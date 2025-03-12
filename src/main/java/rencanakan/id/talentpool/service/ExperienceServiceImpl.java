package rencanakan.id.talentpool.service;

import jakarta.validation.Valid;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;
import rencanakan.id.talentpool.dto.ExperienceRequestDTO;
import rencanakan.id.talentpool.dto.ExperienceResponseDTO;
import rencanakan.id.talentpool.mapper.DTOMapper;
import rencanakan.id.talentpool.model.Experience;
import rencanakan.id.talentpool.repository.ExperienceRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ExperienceServiceImpl implements ExperienceService {

    private final ExperienceRepository experienceRepository;

    public ExperienceServiceImpl(ExperienceRepository experienceRepository) {
        this.experienceRepository = experienceRepository;
    }

    @Override
    public ExperienceResponseDTO createExperience(@Valid ExperienceRequestDTO request) {
        Experience newExperience = DTOMapper.map(request, Experience.class);

        Experience savedExperience = experienceRepository.save(newExperience);

        return DTOMapper.map(savedExperience, ExperienceResponseDTO.class);
    }

    @Override
    public ExperienceResponseDTO editById(Long id, @Valid ExperienceRequestDTO dto) {
        Experience experience = experienceRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Experience with ID " + id + " not found"));

        experience.setTitle(dto.getTitle());
        experience.setCompany(dto.getCompany());
        experience.setEmploymentType(dto.getEmploymentType());
        experience.setStartDate(dto.getStartDate());
        experience.setEndDate(dto.getEndDate());
        experience.setLocation(dto.getLocation());
        experience.setLocationType(dto.getLocationType());

        Experience updatedExperience = experienceRepository.save(experience);

        return DTOMapper.map(updatedExperience, ExperienceResponseDTO.class);
    }

    @Override
    public void deleteById(Long id) {
        Experience experience = experienceRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Experience with ID " + id + " not found"));

        experienceRepository.delete(experience);
    }

    @Override
    public List<ExperienceResponseDTO> getByTalentId(String talentId) {
        List<Experience> experiences = experienceRepository.findByUserId(talentId);

        return experiences.stream()
                .map(experience -> DTOMapper.map(experience, ExperienceResponseDTO.class))
                .collect(Collectors.toList());
    }
}