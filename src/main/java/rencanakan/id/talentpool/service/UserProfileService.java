package rencanakan.id.talentpool.service;

import rencanakan.id.talentpool.dto.UserProfileRequestDTO;
import rencanakan.id.talentpool.dto.UserProfileResponseDTO;
import rencanakan.id.talentpool.model.User;

public interface UserProfileService {
    UserProfileResponseDTO getById(String id);
    UserProfileResponseDTO editProfile(String id, User editedProfile);
//    User createProfile(UserProfileRequestDTO request);

}
