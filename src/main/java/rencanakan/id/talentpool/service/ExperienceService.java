package rencanakan.id.talentpool.service;

import rencanakan.id.talentpool.dto.ExperienceRequestDTO;
import rencanakan.id.talentpool.model.Experience;

public interface ExperienceService {
    Experience createExperience(ExperienceRequestDTO request);
}