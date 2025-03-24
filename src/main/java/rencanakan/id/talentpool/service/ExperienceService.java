package rencanakan.id.talentpool.service;

import rencanakan.id.talentpool.dto.ExperienceRequestDTO;
import rencanakan.id.talentpool.dto.ExperienceResponseDTO;

import java.util.List;

public interface ExperienceService {
    ExperienceResponseDTO createExperience(ExperienceRequestDTO request);
    ExperienceResponseDTO editById(Long id, ExperienceRequestDTO dto);
    void deleteById(Long id);
    List<ExperienceResponseDTO> getByTalentId(String talentId);
}
