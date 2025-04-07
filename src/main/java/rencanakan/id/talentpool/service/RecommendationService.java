package rencanakan.id.talentpool.service;

import rencanakan.id.talentpool.dto.RecommendationRequestDTO;
import rencanakan.id.talentpool.dto.RecommendationResponseDTO;
import rencanakan.id.talentpool.enums.StatusType;

import java.util.List;
import java.util.Map;
    
public interface RecommendationService {
    RecommendationResponseDTO createRecommendation(String talentId, RecommendationRequestDTO recommendation);
    RecommendationResponseDTO editStatusById(String userId, String id, StatusType status);
    RecommendationResponseDTO getById(String id);
    List<RecommendationResponseDTO> getByTalentId(String talentId);
    List<RecommendationResponseDTO> getByTalentIdAndStatus(String talentId, StatusType status);
    Map<StatusType, List<RecommendationResponseDTO>> getByTalentIdAndGroupedByStatus(String talentId);
}
