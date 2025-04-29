package rencanakan.id.talentpool.controller;

import org.springframework.web.bind.annotation.*;
import rencanakan.id.talentpool.dto.UserResponseDTO;
import rencanakan.id.talentpool.mapper.DTOMapper;
import rencanakan.id.talentpool.model.User;
import rencanakan.id.talentpool.repository.UserRepository;
import rencanakan.id.talentpool.service.UserService;

@RestController
@RequestMapping("/ir")
public class IRController {

    private UserRepository userRepository;

    public IRController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @GetMapping("/user-dto")
    public UserResponseDTO getUserDto() {
        User user = userRepository.findById("4f4ddb2c-09a2-411e-b938-5f49acae89d3").orElseThrow();
        return DTOMapper.map(user, UserResponseDTO.class);
    }

    @GetMapping("/user")
    public User getUser() {
        User user = userRepository.findById("4f4ddb2c-09a2-411e-b938-5f49acae89d3").orElseThrow();
        return user;
    }
}