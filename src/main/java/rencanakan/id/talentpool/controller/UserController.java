package rencanakan.id.talentpool.controller;


import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import rencanakan.id.talentpool.dto.*;
import rencanakan.id.talentpool.model.User;
import rencanakan.id.talentpool.service.UserService;

import java.util.List;

@RestController
@RequestMapping("/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/contractor/{id}")
    public ResponseEntity<WebResponse<UserResponseDTO>> getTalentFromContractorById(
            @PathVariable("id") String id) {

        UserResponseDTO userData = userService.getById(id);

        if (userData == null) {
            return ResponseEntity.status(404).body(WebResponse.<UserResponseDTO>builder()
                    .errors("User not found.")
                    .build());
        }

        return ResponseEntity.ok(WebResponse.<UserResponseDTO>builder()
                .data(userData)
                .build());
    }

    @GetMapping("/{id}")
    public ResponseEntity<WebResponse<UserResponseDTO>> getUserById(
            @PathVariable("id") String id, 
            @AuthenticationPrincipal User user) {

        if (user == null) {
            return ResponseEntity.status(401).body(WebResponse.<UserResponseDTO>builder()
                    .errors("Unauthorized access.")
                    .build());
        }

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

    @GetMapping("/contractor")
    public ResponseEntity<WebResponse<List<UserResponseDTO>>> getAllTalent(
            @RequestParam(value = "name", required = false) String name,
            @RequestParam(value = "skills", required = false) List<String> skills,
            @RequestParam(value = "preferred_locations", required = false) List<String> preferredLocations,
            @RequestParam(value = "price_range", required = false) List<Double> priceRange,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") Integer size
    ) {
        FilterTalentDTO filter = FilterTalentDTO.builder().name(name).skills(skills).priceRange(priceRange).preferredLocations(preferredLocations).build();
        Pageable pageable = PageRequest.of(page, size);
        UserResponseWithPagingDTO results = userService.filter(filter, pageable);

        WebResponse<List<UserResponseDTO>> response = WebResponse.<List<UserResponseDTO>>builder()
                .data(results.getUsers())
                .page(results.getPage())
                .size(results.getSize())
                .totalPages(results.getTotalPages())
                .build();

        return ResponseEntity.ok(response);
    }
}
