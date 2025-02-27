package rencanakan.id.talentPool.service;

import rencanakan.id.talentPool.dto.ExperienceDTO;
import rencanakan.id.talentPool.model.Experience;

import java.util.List;

public interface ExperienceServiceInterface {

    List<ExperienceDTO> getExperiencesByTalentId(String talentId);
    public ExperienceDTO addExperience(Experience experience);
}
