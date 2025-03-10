package rencanakan.id.talentPool.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import rencanakan.id.talentPool.dto.UserProfileResponseDTO;
import rencanakan.id.talentPool.dto.WebResponse;
import rencanakan.id.talentPool.model.UserProfile;
import rencanakan.id.talentPool.service.UserProfileService;

@RestController
@RequestMapping("/api/user-profiles")
public class UserProfileController {

    @Autowired
    private UserProfileService userProfileService;

    @GetMapping("/{id}")
    public WebResponse<UserProfileResponseDTO> getUserProfileById(@PathVariable String id, @RequestHeader("Authorization") String token) {
        UserProfileResponseDTO resp = userProfileService.getById(id);
        return WebResponse.<UserProfileResponseDTO>builder()
                .data(resp)
                .build();
    }

    @PutMapping("/edit/{id}")
    public WebResponse<UserProfileResponseDTO> editUserProfile(
            @PathVariable String id,
            @RequestBody UserProfile editedProfile,
            @RequestHeader("Authorization") String token) {

        UserProfileResponseDTO updatedProfile = userProfileService.editProfile(id, editedProfile);

        return WebResponse.<UserProfileResponseDTO>builder()
                .data(updatedProfile)
                .build();
    }
}
