package rencanakan.id.talentpool.service;

import rencanakan.id.talentpool.dto.UserResponseDTO;
import rencanakan.id.talentpool.model.User;

public interface UserService {
    UserResponseDTO getById(String id);
    UserResponseDTO editProfile(String id, User editedProfile);

    User findByEmail(String email);


}
