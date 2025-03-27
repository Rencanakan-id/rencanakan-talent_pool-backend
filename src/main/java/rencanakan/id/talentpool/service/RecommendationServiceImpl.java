package rencanakan.id.talentpool.service;

import jakarta.validation.Valid;
import jakarta.validation.Validator;
import rencanakan.id.talentpool.dto.RecommendationRequestDTO;
import rencanakan.id.talentpool.dto.RecommendationResponseDTO;
import rencanakan.id.talentpool.dto.UserProfileResponseDTO;
import rencanakan.id.talentpool.mapper.DTOMapper;
import rencanakan.id.talentpool.model.Recommendation;
import rencanakan.id.talentpool.model.User;
import rencanakan.id.talentpool.repository.RecommendationRepository;

import java.util.Optional;

public class RecommendationServiceImpl implements RecommendationService {

    private final RecommendationRepository recommendationRepository;
    private final UserProfileService userProfileService;
    private final Validator validator;

    public RecommendationServiceImpl(RecommendationRepository recommendationRepository, UserProfileService userProfileService, Validator validator) {
        this.recommendationRepository = recommendationRepository;
        this.userProfileService = userProfileService;
        this.validator = validator;
    }

    @Override
    public RecommendationResponseDTO createRecommendation(String talentId, @Valid RecommendationRequestDTO recommendation) {
        UserProfileResponseDTO userResponse = userProfileService.getById(talentId);
        User user = DTOMapper.map(userResponse, User.class);

        Recommendation newRecommendation = DTOMapper.map(recommendation, Recommendation.class);
        newRecommendation.setTalent(user);

        Recommendation savedRecommendation = recommendationRepository.save(newRecommendation);
        return DTOMapper.map(savedRecommendation, RecommendationResponseDTO.class);
    }
}
