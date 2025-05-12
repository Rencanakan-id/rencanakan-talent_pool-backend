package rencanakan.id.talentpool.service;

import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import rencanakan.id.talentpool.dto.CertificateRequestDTO;
import rencanakan.id.talentpool.dto.CertificateResponseDTO;
import rencanakan.id.talentpool.mapper.DTOMapper;
import rencanakan.id.talentpool.model.Certificate;
import rencanakan.id.talentpool.model.User;
import rencanakan.id.talentpool.repository.CertificateRepository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class CertificateServiceImpl implements CertificateService {

    private static final Logger logger = LoggerFactory.getLogger(CertificateServiceImpl.class);
    private final CertificateRepository certificateRepository;
    private final PerformanceMonitoringService performanceMonitor;

    public CertificateServiceImpl(CertificateRepository certificateRepository,
                                  PerformanceMonitoringService performanceMonitor) {
        this.certificateRepository = certificateRepository;
        this.performanceMonitor = performanceMonitor;
    }

    @Override
    public List<CertificateResponseDTO> getByUserId(String talentId) {
        Map<String, Object> tags = new HashMap<>();
        tags.put("user.id", talentId);

        return performanceMonitor.trackOperation("certificate.getByUserId", () -> {
            logger.info("Fetching certificates for user ID: {}", talentId);

            List<Certificate> certificates = certificateRepository.findByUserId(talentId);

            if (certificates.isEmpty()) {
                throw new EntityNotFoundException("Certificates not found for user with id " + talentId);
            }

            return certificates.stream()
                    .map(certificate -> DTOMapper.map(certificate, CertificateResponseDTO.class))
                    .toList();
        }, tags);
    }

    @Override
    public CertificateResponseDTO getById(Long certificateId) {
        Map<String, Object> tags = new HashMap<>();
        tags.put("certificate.id", certificateId);

        return performanceMonitor.trackOperation("certificate.getById", () -> {
            logger.info("Fetching certificate with ID: {}", certificateId);

            Certificate certificate = certificateRepository.findById(certificateId)
                    .orElseThrow(() -> new EntityNotFoundException("Certificate with id " + certificateId + " not found"));

            return DTOMapper.map(certificate, CertificateResponseDTO.class);
        }, tags);
    }

    @Override
    public CertificateResponseDTO create(String userId, @Valid CertificateRequestDTO certificateRequest) {
        Map<String, Object> tags = new HashMap<>();
        tags.put("user.id", userId);
        tags.put("certificate.title", certificateRequest != null ? certificateRequest.getTitle() : null);
        tags.put("operation", "create");

        return performanceMonitor.trackOperation("certificate.create", () -> {
            logger.info("Creating certificate for user ID: {}", userId);

            if (userId == null || certificateRequest == null) {
                throw new IllegalArgumentException("Certification request cannot be null");
            }

            Certificate newCertificate = DTOMapper.map(certificateRequest, Certificate.class);
            newCertificate.setUser(User.builder().id(userId).build());

            Certificate savedCertificate = certificateRepository.save(newCertificate);
            return DTOMapper.map(savedCertificate, CertificateResponseDTO.class);
        }, tags);
    }

    @Override
    public CertificateResponseDTO editById(Long id, CertificateRequestDTO dto) {
        Map<String, Object> tags = new HashMap<>();
        tags.put("certificate.id", id);
        tags.put("certificate.title", dto.getTitle());
        tags.put("operation", "update");

        return performanceMonitor.trackOperation("certificate.editById", () -> {
            logger.info("Updating certificate with ID: {}", id);

            Certificate certificate = certificateRepository.findById(id)
                    .orElseThrow(() -> new EntityNotFoundException("Certificate with ID " + id + " not found"));

            certificate.setTitle(dto.getTitle());
            certificate.setFile(dto.getFile());

            Certificate updatedCertificate = certificateRepository.save(certificate);
            return DTOMapper.map(updatedCertificate, CertificateResponseDTO.class);
        }, tags);
    }

    @Override
    public void deleteById(Long id, String userId) throws RuntimeException {
        Map<String, Object> tags = new HashMap<>();
        tags.put("certificate.id", id);
        tags.put("user.id", userId);
        tags.put("operation", "delete");

        performanceMonitor.trackOperation("certificate.deleteById", () -> {
            logger.info("Deleting certificate with ID: {} for user ID: {}", id, userId);

            Certificate certificate = certificateRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Sertifikat tidak ditemukan"));

            if (!certificate.getUser().getId().equals(userId)) {
                throw new RuntimeException("Anda tidak memiliki akses");
            }

            certificateRepository.delete(certificate);
        }, tags);
    }
}