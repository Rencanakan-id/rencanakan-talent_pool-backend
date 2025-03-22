package rencanakan.id.talentpool.service;

import rencanakan.id.talentpool.dto.UserRequestDTO;
import rencanakan.id.talentpool.dto.UserResponseDTO;
import rencanakan.id.talentpool.model.User;

public interface UserService {
    UserResponseDTO getById(String id);
    UserResponseDTO editUser(String id, User editedUser);
    User findByEmail(String email);
}
