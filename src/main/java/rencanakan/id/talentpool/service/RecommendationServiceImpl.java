package rencanakan.id.talentpool.service;

import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;
import rencanakan.id.talentpool.dto.RecommendationResponseDTO;
import rencanakan.id.talentpool.enums.StatusType;
import rencanakan.id.talentpool.mapper.DTOMapper;
import rencanakan.id.talentpool.model.Recommendation;
import rencanakan.id.talentpool.repository.RecommendationRepository;

@Service
public class RecommendationServiceImpl implements RecommendationService{

    private final RecommendationRepository recommendationRepository;

    public RecommendationServiceImpl(RecommendationRepository recommendationRepository) {
        this.recommendationRepository =recommendationRepository;
    }

    @Override
    public RecommendationResponseDTO editStatusById(String id, StatusType status) {
        Recommendation recommendation = recommendationRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Recommendation with ID " + id + " not found"));

        recommendation.setStatus(status);
        recommendationRepository.save(recommendation);
        return  DTOMapper.map(recommendation, RecommendationResponseDTO.class);
    }
}
