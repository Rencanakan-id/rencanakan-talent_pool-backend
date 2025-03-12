package rencanakan.id.talentPool.service;

import rencanakan.id.talentPool.dto.UserProfileRequestDTO;
import rencanakan.id.talentPool.dto.UserProfileResponseDTO;
import rencanakan.id.talentPool.model.User;

public interface UserProfileService {
    UserProfileResponseDTO getById(String id);
    UserProfileResponseDTO editProfile(String id, User editedProfile);
    User createProfile(UserProfileRequestDTO request);

}
