package rencanakan.id.talentpool.service;

import rencanakan.id.talentpool.dto.RecommendationResponseDTO;
import rencanakan.id.talentpool.enums.StatusType;

import java.util.List;
import java.util.Map;

public interface RecommendationService {
    RecommendationResponseDTO getById(String id);
    List<RecommendationResponseDTO> getByStatus(StatusType status);
    List<RecommendationResponseDTO> getAll();
    Map<StatusType, List<RecommendationResponseDTO>> getAllGroupedByStatus();
    List<RecommendationResponseDTO> getByTalentId(String talentId);
    List<RecommendationResponseDTO> getByTalentIdAndStatus(String talentId, StatusType status);
    Map<StatusType, List<RecommendationResponseDTO>> getByTalentIdAndGroupedByStatus(String talentId);
}
