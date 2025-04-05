package rencanakan.id.talentpool.service;

import rencanakan.id.talentpool.dto.CertificateResponseDTO;

import java.util.List;

public interface CertificateService {
    List<CertificateResponseDTO> getByUserId(String talentId);
    CertificateResponseDTO getById(Long certificateId);
    CertificateResponseDTO create(String userId, CertificateResponseDTO certificateRequest);
}
