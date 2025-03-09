package rencanakan.id.talentPool.service;

import rencanakan.id.talentPool.dto.UserProfileResponseDTO;

public interface UserProfileService {
    UserProfileResponseDTO getById(String id);
}
