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
    public RecommendationResponseDTO createRecommendation(User talent, @Valid RecommendationRequestDTO recommendation) {
        if (recommendation == null || talent == null) {
            throw new IllegalArgumentException("Recommendation request cannot be null");
        }

        if (recommendation.getContractorId() == null) {
            throw new IllegalArgumentException("Contractor ID is required");
        }

        if (recommendation.getContractorName() == null) {
            throw new IllegalArgumentException("Contractor name is required");
        }

        if (recommendation.getContractorName().isEmpty()) {
            throw new IllegalArgumentException("Contractor name cannot be empty");
        }

        if (recommendation.getMessage() == null) {
            throw new IllegalArgumentException("Recommendation message is required");
        }

        if (recommendation.getMessage().isEmpty()) {
            throw new IllegalArgumentException("Recommendation message cannot be empty");
        }

        if (recommendation.getMessage().length() > 4000) {
            throw new IllegalArgumentException("Message cannot exceed 4000 characters");
        }

        if (recommendation.getContractorId() <= 0L) {
            throw new IllegalArgumentException("Contractor ID must be greater than 0");
        }


        Recommendation newRecommendation = DTOMapper.map(recommendation, Recommendation.class);
        newRecommendation.setTalent(talent);

        Recommendation savedRecommendation = recommendationRepository.save(newRecommendation);

        return DTOMapper.map(savedRecommendation, RecommendationResponseDTO.class);
    }

}
