package rencanakan.id.talentpool.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import rencanakan.id.talentpool.dto.CertificateResponseDTO;
import rencanakan.id.talentpool.dto.WebResponse;
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
}
