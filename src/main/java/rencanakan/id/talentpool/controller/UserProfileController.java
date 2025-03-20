package rencanakan.id.talentpool.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import rencanakan.id.talentpool.dto.UserProfileResponseDTO;
import rencanakan.id.talentpool.dto.WebResponse;
import rencanakan.id.talentpool.model.User;
import rencanakan.id.talentpool.service.UserService;

@RestController
@RequestMapping("/users")
public class UserProfileController {

    @Autowired
    private UserService userService;

    @GetMapping("/{id}")
    public WebResponse<UserProfileResponseDTO> getUserProfileById(@PathVariable String id, @RequestHeader("Authorization") String token) {
        UserProfileResponseDTO resp = userService.getById(id);
        return WebResponse.<UserProfileResponseDTO>builder()
                .data(resp)
                .build();
    }

    @PutMapping("/{id}")
    public WebResponse<UserProfileResponseDTO> editUserProfile(
            @PathVariable String id,
            @RequestBody User editedProfile,
            @RequestHeader("Authorization") String token) {

        UserProfileResponseDTO updatedProfile = userService.editProfile(id, editedProfile);

        return WebResponse.<UserProfileResponseDTO>builder()
                .data(updatedProfile)
                .build();
    }

//    @PostMapping()
//    public ResponseEntity<User> createExperience(@RequestBody UserProfileRequestDTO request) {
//        try {
//            User createdProfile = userProfileService.createProfile(request);
//            return ResponseEntity.ok(createdProfile);
//        } catch (IllegalArgumentException e) {
//            return ResponseEntity.badRequest().body(null);
//        }
//    }
}
