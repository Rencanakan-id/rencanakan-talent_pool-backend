package rencanakan.id.talentpool.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import rencanakan.id.talentpool.dto.UserProfileResponseDTO;
import rencanakan.id.talentpool.dto.WebResponse;
import rencanakan.id.talentpool.service.UserProfileService;

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
}
