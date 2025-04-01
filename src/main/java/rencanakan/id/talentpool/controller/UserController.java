package rencanakan.id.talentpool.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import rencanakan.id.talentpool.dto.UserResponseDTO;
import rencanakan.id.talentpool.dto.WebResponse;
import rencanakan.id.talentpool.model.User;
import rencanakan.id.talentpool.service.UserService;

@RestController
@RequestMapping("/users")
public class UserController {

    @Autowired
    private UserService userService;

    @GetMapping("/{id}")
    public WebResponse<UserResponseDTO> getUserProfileById(
            @PathVariable("id") String id, 
            @RequestHeader("Authorization") String token) {
                
        UserResponseDTO resp = userService.getById(id);
        return WebResponse.<UserResponseDTO>builder()
                .data(resp)
                .build();
    }

    @PutMapping("/{id}")
    public WebResponse<UserResponseDTO> editUserProfile(
            @PathVariable("id") String id,
            @RequestBody User editedProfile,
            @RequestHeader("Authorization") String token) {

        UserResponseDTO updatedProfile = userService.editUser(id, editedProfile);

        return WebResponse.<UserResponseDTO>builder()
                .data(updatedProfile)
                .build();
    }
}
