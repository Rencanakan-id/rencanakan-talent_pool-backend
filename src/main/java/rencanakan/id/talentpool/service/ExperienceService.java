package rencanakan.id.talentpool.service;

import rencanakan.id.talentpool.dto.ExperienceRequestDTO;
import rencanakan.id.talentpool.dto.ExperienceResponseDTO;

import java.util.List;

public interface ExperienceService {
    ExperienceResponseDTO createExperience(String userId, ExperienceRequestDTO request);
    ExperienceResponseDTO editById(String userId, Long id, ExperienceRequestDTO dto);
    void deleteById(Long id);
    List<ExperienceResponseDTO> getByTalentId(String talentId);
}
