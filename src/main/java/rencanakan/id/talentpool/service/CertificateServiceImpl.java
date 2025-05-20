package rencanakan.id.talentpool.service;

import jakarta.validation.Valid;
import org.springframework.stereotype.Service;

import jakarta.persistence.EntityNotFoundException;
import rencanakan.id.talentpool.dto.CertificateRequestDTO;
import rencanakan.id.talentpool.dto.CertificateResponseDTO;
import rencanakan.id.talentpool.mapper.DTOMapper;
import rencanakan.id.talentpool.model.Certificate;
import rencanakan.id.talentpool.model.User;
import rencanakan.id.talentpool.repository.CertificateRepository;

import java.util.List;

@Service
public class CertificateServiceImpl implements CertificateService {

    private final CertificateRepository certificateRepository;

    public CertificateServiceImpl(CertificateRepository certificateRepository) {
        this.certificateRepository = certificateRepository;
    }

    @Override
    public List<CertificateResponseDTO> getByUserId(String talentId) {

        List<Certificate> certificates = certificateRepository.findByUserId(talentId);
        if (certificates.isEmpty()) {
            throw new EntityNotFoundException("Certificates not found for user with id " + talentId);
        }

        return certificates.stream()
            .map(certificate -> DTOMapper.map(certificate, CertificateResponseDTO.class))
            .toList();
    }

    @Override
    public CertificateResponseDTO getById(Long certificateId) {

        Certificate certificate = certificateRepository.findById(certificateId)
                .orElseThrow(() -> new EntityNotFoundException("Certificate with id " + certificateId + " not found"));

        return DTOMapper.map(certificate, CertificateResponseDTO.class);
    }

    @Override
    public CertificateResponseDTO create(String userId, @Valid CertificateRequestDTO certificateRequest) {

        if (userId == null || certificateRequest == null) {
            throw new IllegalArgumentException("Certification request cannot be null");
        }

        Certificate newCertificate = DTOMapper.map(certificateRequest, Certificate.class);
        newCertificate.setUser(User.builder().id(userId).build());

        Certificate savedCertificate = certificateRepository.save(newCertificate);
        return DTOMapper.map(savedCertificate, CertificateResponseDTO.class);
    }

    @Override
    public CertificateResponseDTO editById(Long id, CertificateRequestDTO dto){
        Certificate certificate = certificateRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Certificate with ID " + id + " not found"));

        certificate.setTitle(dto.getTitle());
        certificate.setFile(dto.getFile());

        Certificate updatedCertificate = certificateRepository.save(certificate);

        return DTOMapper.map(updatedCertificate, CertificateResponseDTO.class);
    }

    public void deleteById(Long id, String userId) throws RuntimeException {
        Certificate certificate = certificateRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Sertifikat tidak ditemukan"));

        if (!certificate.getUser().getId().equals(userId)) {
            throw new RuntimeException("Anda tidak memiliki akses");
        }
        certificateRepository.delete(certificate);
    }
}
