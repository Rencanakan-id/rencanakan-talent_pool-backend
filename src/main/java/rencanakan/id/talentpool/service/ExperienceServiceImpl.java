package rencanakan.id.talentpool.service;

import jakarta.validation.Valid;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;
import rencanakan.id.talentpool.dto.ExperienceRequestDTO;
import rencanakan.id.talentpool.dto.ExperienceResponseDTO;
import rencanakan.id.talentpool.mapper.DTOMapper;
import rencanakan.id.talentpool.model.Experience;
import rencanakan.id.talentpool.repository.ExperienceRepository;

@Service
public class ExperienceServiceImpl implements ExperienceService {

    private final ExperienceRepository experienceRepository;

    public ExperienceServiceImpl(ExperienceRepository experienceRepository) {
        this.experienceRepository = experienceRepository;
    }

    @Override
    public ExperienceResponseDTO createExperience(@Valid ExperienceRequestDTO request) {
        Experience newExperience = DTOMapper.map(request, Experience.class);
//        newExperience.setUser(new User(request.getTalentId()));

        Experience savedExperience = experienceRepository.save(newExperience);
        return DTOMapper.map(savedExperience, ExperienceResponseDTO.class);
    }

    @Override
    public ExperienceResponseDTO editById(Long id, ExperienceRequestDTO dto) {
        Experience experience = experienceRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Experience not found"));

        Experience new_exp =  experienceRepository.save(experience);
        return DTOMapper.map(new_exp, ExperienceResponseDTO.class);
    }
}