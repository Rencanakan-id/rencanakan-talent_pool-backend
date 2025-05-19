package rencanakan.id.talentpool.controller;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import rencanakan.id.talentpool.service.EmailService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/email")
public class EmailController {

    private final EmailService emailService;

    @PostMapping
    public ResponseEntity<String> sendResetPassword(@RequestBody ResetPasswordRequest request) {
        try {
            emailService.processResetPassword(request.email());
            return ResponseEntity.ok("Reset password email sent.");
        } catch (EntityNotFoundException ex) {
            return ResponseEntity
                    .badRequest()
                    .body("Email tidak ditemukan: " + request.email());
        }
    }

    public record ResetPasswordRequest(String email) {}
}
