package rencanakan.id.talentpool.controller;

import jakarta.persistence.EntityNotFoundException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import rencanakan.id.talentpool.service.EmailService;

@RestController
@RequestMapping("/email")
public class EmailController {

    private final EmailService emailService;

    public EmailController(EmailService emailService) {
        this.emailService = emailService;
    }

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
