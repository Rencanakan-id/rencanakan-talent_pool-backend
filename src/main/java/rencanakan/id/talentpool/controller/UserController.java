package rencanakan.id.talentpool.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import rencanakan.id.talentpool.dto.UserRequestDTO;
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
    public ResponseEntity<WebResponse<UserResponseDTO>> getUserById(
            @PathVariable("id") String id, 
            @RequestHeader("Authorization") String token) {

        UserResponseDTO resp = userService.getById(id);

        if (resp == null) {
            return ResponseEntity.status(404).body(WebResponse.<UserResponseDTO>builder()
                    .errors("User not found.")
                    .build());
        }

        return ResponseEntity.ok(WebResponse.<UserResponseDTO>builder()
                .data(resp)
                .build());
    }

    @GetMapping("/me")
    public ResponseEntity<WebResponse<UserResponseDTO>> getCurrentUser(
            @AuthenticationPrincipal User user) {

        if (user == null) {
            return ResponseEntity.status(401).body(WebResponse.<UserResponseDTO>builder()
                    .errors("Unauthorized access.")
                    .build());
        }

        UserResponseDTO resp = userService.getById(user.getId());

		if (resp == null) {
			return ResponseEntity.status(404).body(WebResponse.<UserResponseDTO>builder()
					.errors("User not found.")
					.build());
		}
        
        return ResponseEntity.ok(WebResponse.<UserResponseDTO>builder()
                .data(resp)
                .build());
    }

    @PutMapping("/{id}")
    public ResponseEntity<WebResponse<UserResponseDTO>> editUserById(
            @PathVariable("id") String id,
            @RequestBody @Valid UserRequestDTO editedUser,
            @AuthenticationPrincipal User user) {

        if (!user.getId().equals(id)) {
            return ResponseEntity.status(403).body(WebResponse.<UserResponseDTO>builder()
                    .errors("You are not authorized to edit this user.")
                    .build());
        }

        UserResponseDTO updatedProfile = userService.editById(id, editedUser);

        return ResponseEntity.ok(WebResponse.<UserResponseDTO>builder()
                .data(updatedProfile)
                .build());
    }
}
