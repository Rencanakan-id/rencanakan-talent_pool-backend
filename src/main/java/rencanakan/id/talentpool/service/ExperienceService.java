package rencanakan.id.talentpool.service;

import rencanakan.id.talentpool.dto.ExperienceRequestDTO;
import rencanakan.id.talentpool.dto.ExperienceResponseDTO;
import rencanakan.id.talentpool.model.Experience;

public interface ExperienceService {
    ExperienceResponseDTO createExperience(ExperienceRequestDTO request);
    ExperienceResponseDTO editById(Long id, ExperienceRequestDTO dto);
}