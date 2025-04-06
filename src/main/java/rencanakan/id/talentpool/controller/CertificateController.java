package rencanakan.id.talentpool.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import rencanakan.id.talentpool.dto.CertificateResponseDTO;
import rencanakan.id.talentpool.dto.WebResponse;
import rencanakan.id.talentpool.model.User;
import rencanakan.id.talentpool.service.CertificateService;

import java.util.List;

@RestController
@RequestMapping("/certificates")
public class CertificateController {
    
    private final CertificateService certificateService;

    public CertificateController(CertificateService certificateService) {
        this.certificateService = certificateService;
    }
    
    @GetMapping("/user/{userId}")
    public ResponseEntity<WebResponse<List<CertificateResponseDTO>>> getCertificatesByUserId(
            @PathVariable("userId") String userId,
            @AuthenticationPrincipal User user) {

        if (user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(WebResponse.<List<CertificateResponseDTO>>builder()
                    .errors("Unauthorized access")
                    .build());
        }

        List<CertificateResponseDTO> certificates = certificateService.getByUserId(userId);

        if (certificates.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(WebResponse.<List<CertificateResponseDTO>>builder()
                    .errors("No certificates found for user ID: " + userId)
                    .build());
        }

        return ResponseEntity.ok(WebResponse.<List<CertificateResponseDTO>>builder()
                .data(certificates)
                .build());
    }
    
    @GetMapping("/{certificateId}")
    public ResponseEntity<WebResponse<CertificateResponseDTO>> getCertificateById(
            @PathVariable("certificateId") Long certificateId,
            @AuthenticationPrincipal User user) {

        if (user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(WebResponse.<CertificateResponseDTO>builder()
                    .errors("Unauthorized access")
                    .build());
        }

        CertificateResponseDTO certificate = certificateService.getById(certificateId);
        
        if (certificate == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(WebResponse.<CertificateResponseDTO>builder()
                    .errors("Certificate not found with ID: " + certificateId)
                    .build());
        }

        return ResponseEntity.ok(WebResponse.<CertificateResponseDTO>builder()
                .data(certificate)
                .build());
    }
}
