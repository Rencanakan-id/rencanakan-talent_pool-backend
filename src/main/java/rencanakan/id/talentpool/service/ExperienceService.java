package rencanakan.id.talentpool.service;

import rencanakan.id.talentpool.dto.ExperienceRequestDTO;
import rencanakan.id.talentpool.dto.ExperienceResponseDTO;
import rencanakan.id.talentpool.model.Experience;

import java.util.List;

public interface ExperienceService {
    ExperienceResponseDTO createExperience(String userId, ExperienceRequestDTO request);
    ExperienceResponseDTO editById(Long id, ExperienceRequestDTO dto);
    void deleteById(Long id);
    List<ExperienceResponseDTO> getByTalentId(String talentId);
}
