package rencanakan.id.talentpool.service;

import rencanakan.id.talentpool.dto.RecommendationResponseDTO;
import rencanakan.id.talentpool.model.Recommendation;

public interface RecommendationService {
    RecommendationResponseDTO createRecommendation(Recommendation recommendation);
}
