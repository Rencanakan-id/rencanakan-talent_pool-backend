package rencanakan.id.talentpool.service;

import rencanakan.id.talentpool.dto.UserProfileResponseDTO;

public interface UserProfileService {
    UserProfileResponseDTO getById(String id);
}
