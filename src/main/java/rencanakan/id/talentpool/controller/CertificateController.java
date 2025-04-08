package rencanakan.id.talentpool.controller;

import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import rencanakan.id.talentpool.dto.CertificateRequestDTO;
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
            @RequestHeader("Authorization") String token) {

        List<CertificateResponseDTO> certificates = certificateService.getByUserId(userId);
        return ResponseEntity.ok(WebResponse.<List<CertificateResponseDTO>>builder()
                .data(certificates)
                .build());
    }
    
    @GetMapping("/{certificateId}")
    public ResponseEntity<WebResponse<CertificateResponseDTO>> getCertificateById(
            @PathVariable("certificateId") Long certificateId,
            @RequestHeader("Authorization") String token) {

        CertificateResponseDTO certificate = certificateService.getById(certificateId);
        return ResponseEntity.ok(WebResponse.<CertificateResponseDTO>builder()
                .data(certificate)
                .build());
    }

    @PostMapping
    public ResponseEntity<WebResponse<CertificateResponseDTO>> createCertificate(
            @RequestBody @Valid CertificateRequestDTO certificateRequest,
            @AuthenticationPrincipal User user) {

        CertificateResponseDTO createdCertificate = certificateService.create(user.getId(), certificateRequest);
        return ResponseEntity.ok(WebResponse.<CertificateResponseDTO>builder()
                .data(createdCertificate)
                .build());
    }

    @PutMapping("/{certificateId}")
    public ResponseEntity<WebResponse<CertificateResponseDTO>> editCertificateById(
            @PathVariable Long certificateId,
            @AuthenticationPrincipal User user,
            @RequestBody @Valid CertificateRequestDTO dto) {

        try {
            CertificateResponseDTO updatedCertificate = certificateService.editById(certificateId, dto);
            return ResponseEntity.ok(WebResponse.<CertificateResponseDTO>builder()
                    .data(updatedCertificate)
                    .build());
        } catch (Exception e) {
            return ResponseEntity.status(404).body(WebResponse.<CertificateResponseDTO>builder()
                    .errors("Certificate not found with ID: " + certificateId)
                    .build());
        }
    }
}
