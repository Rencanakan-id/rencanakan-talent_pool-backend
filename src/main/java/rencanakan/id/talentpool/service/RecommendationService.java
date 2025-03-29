package rencanakan.id.talentpool.service;

import rencanakan.id.talentpool.dto.RecommendationRequestDTO;
import rencanakan.id.talentpool.dto.RecommendationResponseDTO;
import rencanakan.id.talentpool.model.User;

public interface RecommendationService {
    RecommendationResponseDTO createRecommendation(User talent , RecommendationRequestDTO recommendation);
}
