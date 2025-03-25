package rencanakan.id.talentpool.service;

import jakarta.validation.Validator;
import rencanakan.id.talentpool.dto.RecommendationResponseDTO;
import rencanakan.id.talentpool.model.Recommendation;
import rencanakan.id.talentpool.model.User;
import rencanakan.id.talentpool.repository.RecommendationRepository;

import java.util.Optional;

public class RecommendationServiceImpl implements RecommendationService {

    private final RecommendationRepository recommendationRepository;
    private final Validator validator;

    public RecommendationServiceImpl(RecommendationRepository recommendationRepository, Validator validator) {
        this.recommendationRepository = recommendationRepository;
        this.validator = validator;
    }

    @Override
    public RecommendationResponseDTO createRecommendation(Recommendation recommendation) {
        return null;
    }
}
