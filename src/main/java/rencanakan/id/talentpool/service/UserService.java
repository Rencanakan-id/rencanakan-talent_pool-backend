package rencanakan.id.talentpool.service;

import org.springframework.data.domain.Pageable;
import rencanakan.id.talentpool.dto.FilterTalentDTO;
import rencanakan.id.talentpool.dto.UserRequestDTO;
import rencanakan.id.talentpool.dto.UserResponseDTO;
import rencanakan.id.talentpool.dto.UserResponseWithPagingDTO;
import rencanakan.id.talentpool.model.User;


import java.util.List;

public interface UserService {
    UserResponseDTO getById(String id);
    UserResponseDTO editById(String id, UserRequestDTO editedUser);
    User findByEmail(String email);
    UserResponseWithPagingDTO filter(FilterTalentDTO filter, Pageable page);
}
