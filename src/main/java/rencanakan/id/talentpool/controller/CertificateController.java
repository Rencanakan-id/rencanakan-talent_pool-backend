package rencanakan.id.talentpool.controller;

import org.springframework.http.HttpStatus;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import rencanakan.id.talentpool.dto.CertificateRequestDTO;
import rencanakan.id.talentpool.dto.CertificateResponseDTO;
import rencanakan.id.talentpool.dto.WebResponse;
import rencanakan.id.talentpool.model.User;
import rencanakan.id.talentpool.service.CertificateService;
import rencanakan.id.talentpool.service.MetricsService;

import java.util.List;
import java.util.Objects;

@RestController
@RequestMapping("/certificates")
public class CertificateController {

    private final CertificateService certificateService;
    private final MetricsService metricsService;

    public CertificateController(CertificateService certificateService, MetricsService metricsService) {
        this.certificateService = certificateService;
        this.metricsService = metricsService;
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<WebResponse<List<CertificateResponseDTO>>> getCertificatesByUserId(
            @PathVariable("userId") String userId,
            @AuthenticationPrincipal User user) {

        long startTime = System.currentTimeMillis();
        String endpoint = "/certificates/user/{userId}";

        // Track API request
        metricsService.trackApiRequest(endpoint, "GET", user != null ? user.getId() : "anonymous");

        if (user == null) {
            metricsService.trackError(endpoint, "unauthorized");
            metricsService.trackApiResponse(endpoint, HttpStatus.UNAUTHORIZED.value(), System.currentTimeMillis() - startTime);

            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(WebResponse.<List<CertificateResponseDTO>>builder()
                    .errors("Unauthorized access")
                    .build());
        }

        try {
            List<CertificateResponseDTO> certificates = certificateService.getByUserId(userId);

            // Track business metrics
            metricsService.trackCertificateOperation("view_list", user.getId());
            metricsService.trackCertificateCount(userId, certificates.size());

            // Track API response
            metricsService.trackApiResponse(endpoint, HttpStatus.OK.value(), System.currentTimeMillis() - startTime);

            return ResponseEntity.ok(WebResponse.<List<CertificateResponseDTO>>builder()
                    .data(certificates)
                    .build());
        } catch (Exception e) {
            // Track error
            metricsService.trackError(endpoint, "not_found");
            metricsService.trackApiResponse(endpoint, HttpStatus.NOT_FOUND.value(), System.currentTimeMillis() - startTime);

            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(WebResponse.<List<CertificateResponseDTO>>builder()
                    .errors(e.getMessage())
                    .build());
        }
    }

    @GetMapping("/{certificateId}")
    public ResponseEntity<WebResponse<CertificateResponseDTO>> getCertificateById(
            @PathVariable("certificateId") Long certificateId,
            @AuthenticationPrincipal User user) {

        long startTime = System.currentTimeMillis();
        String endpoint = "/certificates/{certificateId}";

        // Track API request
        metricsService.trackApiRequest(endpoint, "GET", user != null ? user.getId() : "anonymous");

        if (user == null) {
            metricsService.trackError(endpoint, "unauthorized");
            metricsService.trackApiResponse(endpoint, HttpStatus.UNAUTHORIZED.value(), System.currentTimeMillis() - startTime);

            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(WebResponse.<CertificateResponseDTO>builder()
                    .errors("Unauthorized access")
                    .build());
        }

        try {
            CertificateResponseDTO certificate = certificateService.getById(certificateId);

            // Track business metrics
            metricsService.trackCertificateOperation("view_detail", user.getId());

            // Track API response
            metricsService.trackApiResponse(endpoint, HttpStatus.OK.value(), System.currentTimeMillis() - startTime);

            return ResponseEntity.ok(WebResponse.<CertificateResponseDTO>builder()
                    .data(certificate)
                    .build());
        } catch (Exception e) {
            // Track error
            metricsService.trackError(endpoint, "not_found");
            metricsService.trackApiResponse(endpoint, HttpStatus.NOT_FOUND.value(), System.currentTimeMillis() - startTime);

            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(WebResponse.<CertificateResponseDTO>builder()
                    .errors(e.getMessage())
                    .build());
        }
    }

    @PostMapping
    public ResponseEntity<WebResponse<CertificateResponseDTO>> createCertificate(
            @RequestBody @Valid CertificateRequestDTO certificateRequest,
            @AuthenticationPrincipal User user) {

        long startTime = System.currentTimeMillis();
        String endpoint = "/certificates";

        // Track API request
        metricsService.trackApiRequest(endpoint, "POST", user.getId());

        try {
            CertificateResponseDTO createdCertificate = certificateService.create(user.getId(), certificateRequest);

            // Track business metrics
            metricsService.trackCertificateOperation("create", user.getId());

            // Track certificate title length as a histogram (useful for UI optimization)
            metricsService.histogram("certificate.title_length",
                    certificateRequest.getTitle().length(),
                    "user_id:" + user.getId()
            );

            // Track API response
            metricsService.trackApiResponse(endpoint, HttpStatus.OK.value(), System.currentTimeMillis() - startTime);

            return ResponseEntity.ok(WebResponse.<CertificateResponseDTO>builder()
                    .data(createdCertificate)
                    .build());
        } catch (Exception e) {
            // Track error
            metricsService.trackError(endpoint, "creation_failed");
            metricsService.trackApiResponse(endpoint, HttpStatus.BAD_REQUEST.value(), System.currentTimeMillis() - startTime);

            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(WebResponse.<CertificateResponseDTO>builder()
                    .errors(e.getMessage())
                    .build());
        }
    }

    @PutMapping("/{certificateId}")
    public ResponseEntity<WebResponse<CertificateResponseDTO>> editCertificateById(
            @PathVariable("certificateId") Long certificateId,
            @AuthenticationPrincipal User user,
            @RequestBody @Valid CertificateRequestDTO dto) {

        long startTime = System.currentTimeMillis();
        String endpoint = "/certificates/{certificateId}";

        // Track API request
        metricsService.trackApiRequest(endpoint, "PUT", user.getId());

        try {
            CertificateResponseDTO updatedCertificate = certificateService.editById(certificateId, dto);

            // Track business metrics
            metricsService.trackCertificateOperation("update", user.getId());

            // Track API response
            metricsService.trackApiResponse(endpoint, HttpStatus.OK.value(), System.currentTimeMillis() - startTime);

            return ResponseEntity.ok(WebResponse.<CertificateResponseDTO>builder()
                    .data(updatedCertificate)
                    .build());
        } catch (Exception e) {
            // Track error
            metricsService.trackError(endpoint, "update_failed");
            metricsService.trackApiResponse(endpoint, HttpStatus.NOT_FOUND.value(), System.currentTimeMillis() - startTime);

            return ResponseEntity.status(404).body(WebResponse.<CertificateResponseDTO>builder()
                    .errors("Certificate not found with ID: " + certificateId)
                    .build());
        }
    }

    @DeleteMapping("/{certificateId}")
    public ResponseEntity<WebResponse<String>> deleteCertificateById(
            @PathVariable("certificateId") Long certificateId,
            @AuthenticationPrincipal User user) {

        long startTime = System.currentTimeMillis();
        String endpoint = "/certificates/{certificateId}";

        // Track API request
        metricsService.trackApiRequest(endpoint, "DELETE", user.getId());

        try {
            certificateService.deleteById(certificateId, user.getId());

            // Track business metrics
            metricsService.trackCertificateOperation("delete", user.getId());

            // Track API response
            metricsService.trackApiResponse(endpoint, HttpStatus.OK.value(), System.currentTimeMillis() - startTime);

            return ResponseEntity.ok(WebResponse.<String>builder()
                    .data("Sertifikat berhasil dihapus.")
                    .build());
        } catch (RuntimeException e) {
            String errorType = "delete_failed";
            int statusCode = HttpStatus.INTERNAL_SERVER_ERROR.value();

            if (Objects.equals(e.getMessage(), "Sertifikat tidak ditemukan")) {
                errorType = "not_found";
                statusCode = HttpStatus.NOT_FOUND.value();
            } else if (e.getMessage().contains("tidak memiliki akses")) {
                errorType = "forbidden";
                statusCode = HttpStatus.FORBIDDEN.value();
            }

            // Track error
            metricsService.trackError(endpoint, errorType);
            metricsService.trackApiResponse(endpoint, statusCode, System.currentTimeMillis() - startTime);

            if (Objects.equals(e.getMessage(), "Sertifikat tidak ditemukan")) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(WebResponse.<String>builder()
                        .errors(e.getMessage())
                        .build());
            } else if (e.getMessage().contains("tidak memiliki akses")) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(WebResponse.<String>builder()
                        .errors(e.getMessage())
                        .build());
            }

            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(WebResponse.<String>builder()
                    .errors("An error occurred while deleting the certificate")
                    .build());
        }
    }
}