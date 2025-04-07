package rencanakan.id.talentpool.service;

import rencanakan.id.talentpool.dto.RecommendationRequestDTO;
import rencanakan.id.talentpool.dto.RecommendationResponseDTO;

public interface RecommendationService {
    RecommendationResponseDTO editById(Long id, RecommendationRequestDTO dto);
}
