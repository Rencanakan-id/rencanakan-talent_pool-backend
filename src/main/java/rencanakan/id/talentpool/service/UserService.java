package rencanakan.id.talentpool.service;

import rencanakan.id.talentpool.dto.UserProfileResponseDTO;
import rencanakan.id.talentpool.model.User;

public interface UserService {
    UserProfileResponseDTO getById(String id);
    UserProfileResponseDTO editProfile(String id, User editedProfile);

    User findByEmail(String email);


}
