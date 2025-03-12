package rencanakan.id.talentpool.service;

import rencanakan.id.talentpool.dto.EditExperienceRequestDTO;
import rencanakan.id.talentpool.dto.ExperienceListResponseDTO;
import rencanakan.id.talentpool.dto.ExperienceResponseDTO;

public interface ExperienceService{
    ExperienceResponseDTO editById(Long id, EditExperienceRequestDTO dto);

    ExperienceListResponseDTO getByTalentId(Long id);
}
