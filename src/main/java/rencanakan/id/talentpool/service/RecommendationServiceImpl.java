package rencanakan.id.talentpool.service;

import jakarta.validation.Valid;
import rencanakan.id.talentpool.dto.RecommendationRequestDTO;
import rencanakan.id.talentpool.dto.RecommendationResponseDTO;
import org.springframework.stereotype.Service;

import jakarta.persistence.EntityNotFoundException;
import rencanakan.id.talentpool.enums.StatusType;
import rencanakan.id.talentpool.mapper.DTOMapper;
import rencanakan.id.talentpool.model.Recommendation;
import rencanakan.id.talentpool.model.User;
import rencanakan.id.talentpool.repository.RecommendationRepository;
import rencanakan.id.talentpool.repository.UserRepository;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;

@Service
public class RecommendationServiceImpl implements RecommendationService {
    
    private final RecommendationRepository recommendationRepository;
    private final UserRepository userRepository;

    public RecommendationServiceImpl(RecommendationRepository recommendationRepository, UserRepository userRepository) {
        this.recommendationRepository = recommendationRepository;
        this.userRepository = userRepository;
    }
    
    @Override
    public RecommendationResponseDTO getById(String id) {
        Recommendation recommendation = recommendationRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Recommendation not found with id: " + id));
        
        return DTOMapper.map(recommendation, RecommendationResponseDTO.class);
    }
    
    @Override
    public List<RecommendationResponseDTO> getByTalentId(String talentId) {
        User talent = userRepository.findById(talentId)
                .orElseThrow(() -> new EntityNotFoundException("User not found with id: " + talentId));

        List<Recommendation> recommendations = recommendationRepository.findByTalent(talent);
        
        return recommendations.stream()
            .map(recommendation -> DTOMapper.map(recommendation, RecommendationResponseDTO.class))
            .toList();
    }

    @Override
    public List<RecommendationResponseDTO> getByTalentIdAndStatus(String talentId, StatusType status) {
        User talent = userRepository.findById(talentId)
                .orElseThrow(() -> new EntityNotFoundException("User not found with id: " + talentId));

        List<Recommendation> recommendations = recommendationRepository.findByTalentAndStatus(talent, status);
        
        return recommendations.stream()
                .map(recommendation -> DTOMapper.map(recommendation, RecommendationResponseDTO.class))
                .toList();
    }
    
    @Override
    public Map<StatusType, List<RecommendationResponseDTO>> getByTalentIdAndGroupedByStatus(String talentId) {
        Map<StatusType, List<RecommendationResponseDTO>> groupedRecommendations = new EnumMap<>(StatusType.class);
        
        for (StatusType status : StatusType.values()) {
            groupedRecommendations.put(status, getByTalentIdAndStatus(talentId, status));
        }
        
        return groupedRecommendations;
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
