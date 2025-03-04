package rencanakan.id.talentPool.service;

import rencanakan.id.talentPool.dto.ExperienceRequestDTO;
import rencanakan.id.talentPool.model.Experience;

public interface ExperienceService {
    Experience createExperience(ExperienceRequestDTO request);
}