package rencanakan.id.talentpool.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import rencanakan.id.talentpool.dto.UserResponseDTO;
import rencanakan.id.talentpool.dto.WebResponse;
import rencanakan.id.talentpool.model.User;
import rencanakan.id.talentpool.service.UserService;

@RestController
@RequestMapping("/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/{id}")
    public WebResponse<UserResponseDTO> getUserById(
            @PathVariable String id, 
            @RequestHeader("Authorization") String token) {
        
        UserResponseDTO resp = userService.getById(id);
        
        return WebResponse.<UserResponseDTO>builder()
                .data(resp)
                .build();
    }

    @PutMapping("/{id}")
    public WebResponse<UserResponseDTO> editUser(
            @PathVariable String id,
            @RequestBody User editedUser,
            @RequestHeader("Authorization") String token) {

        UserResponseDTO updatedUser = userService.editUser(id, editedUser);

        return WebResponse.<UserResponseDTO>builder()
                .data(updatedUser)
                .build();
    }

    @GetMapping("/me")
    public ResponseEntity<User> authenticatedUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDetails currentUserDetails = (UserDetails) authentication.getPrincipal();
        User currentUser = userService.findByEmail(currentUserDetails.getUsername());
        return ResponseEntity.ok(currentUser);
    }
}
