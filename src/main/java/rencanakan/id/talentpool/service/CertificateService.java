package rencanakan.id.talentpool.service;

import rencanakan.id.talentpool.dto.CertificateRequestDTO;
import rencanakan.id.talentpool.dto.CertificateResponseDTO;

import java.util.List;

public interface CertificateService {
    List<CertificateResponseDTO> getByUserId(String talentId);
    CertificateResponseDTO getById(Long certificateId);
    CertificateResponseDTO editById(Long id, CertificateRequestDTO dto);
    CertificateResponseDTO create(String userId, CertificateRequestDTO certificateRequest);
}
