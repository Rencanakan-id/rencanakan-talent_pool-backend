package rencanakan.id.talentPool.service;

import rencanakan.id.talentPool.dto.UserProfileResponseDTO;
import rencanakan.id.talentPool.model.UserProfile;

public interface UserProfileService {
    UserProfileResponseDTO getById(String id);
    UserProfileResponseDTO editProfile(String id, UserProfile editedProfile);
}
