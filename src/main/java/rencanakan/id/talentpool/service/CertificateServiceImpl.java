package rencanakan.id.talentpool.service;

import org.springframework.stereotype.Service;

import jakarta.persistence.EntityNotFoundException;
import rencanakan.id.talentpool.dto.CertificateResponseDTO;
import rencanakan.id.talentpool.mapper.DTOMapper;
import rencanakan.id.talentpool.model.Certificate;
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
}
