package rencanakan.id.talentPool.service;

import rencanakan.id.talentPool.dto.TalentDTO;
import rencanakan.id.talentPool.model.Talent;

public interface TalentServiceInterface {
    public TalentDTO getTalentDTOById(String id);

    public TalentDTO addTalent(TalentDTO talentDTO);
}
