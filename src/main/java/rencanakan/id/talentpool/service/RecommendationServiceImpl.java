package rencanakan.id.talentpool.service;

import jakarta.validation.Valid;
import jakarta.validation.Validator;
import rencanakan.id.talentpool.dto.RecommendationRequestDTO;
import rencanakan.id.talentpool.dto.RecommendationResponseDTO;
import rencanakan.id.talentpool.mapper.DTOMapper;
import rencanakan.id.talentpool.model.Recommendation;
import rencanakan.id.talentpool.model.User;
import rencanakan.id.talentpool.repository.RecommendationRepository;
import rencanakan.id.talentpool.repository.UserProfileRepository;

import java.util.Optional;

public class RecommendationServiceImpl implements RecommendationService {

    private final RecommendationRepository recommendationRepository;

    public RecommendationServiceImpl(RecommendationRepository recommendationRepository) {
        this.recommendationRepository = recommendationRepository;
    }

    @Override
    public RecommendationResponseDTO createRecommendation(String talentId, @Valid RecommendationRequestDTO recommendation) {

        if (talentId == null || recommendation == null) {
            throw new IllegalArgumentException("Recommendation request cannot be null");
        }

        Recommendation newRecommendation = DTOMapper.map(recommendation, Recommendation.class);
        newRecommendation.setTalent(User.builder().id(talentId).build());

        Recommendation savedRecommendation = recommendationRepository.save(newRecommendation);

        return DTOMapper.map(savedRecommendation, RecommendationResponseDTO.class);
    }

}
