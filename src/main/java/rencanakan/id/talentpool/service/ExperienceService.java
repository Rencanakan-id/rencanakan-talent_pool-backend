package rencanakan.id.talentpool.service;

import rencanakan.id.talentpool.dto.ExperienceRequestDTO;
import rencanakan.id.talentpool.dto.ExperienceResponseDTO;
import rencanakan.id.talentpool.model.Experience;

import java.util.List;

public interface ExperienceService {
    ExperienceResponseDTO createExperience(ExperienceRequestDTO request);
    ExperienceResponseDTO editById(String userId, Long id, ExperienceRequestDTO dto);
    void deleteById(Long id);
    List<ExperienceResponseDTO> getByTalentId(String talentId);
}
