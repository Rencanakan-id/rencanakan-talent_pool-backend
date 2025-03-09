package rencanakan.id.talentPool.service;

import rencanakan.id.talentPool.dto.EditExperienceRequestDTO;
import rencanakan.id.talentPool.dto.ExperienceListResponseDTO;
import rencanakan.id.talentPool.dto.ExperienceResponseDTO;

public interface ExperienceService{
    ExperienceResponseDTO editById(Long id, EditExperienceRequestDTO dto);

    ExperienceListResponseDTO getByTalentId(Long id);

    void deleteById(Long id);
}
