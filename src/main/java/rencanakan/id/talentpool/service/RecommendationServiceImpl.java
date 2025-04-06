package rencanakan.id.talentpool.service;

import org.springframework.stereotype.Service;

import jakarta.persistence.EntityNotFoundException;
import rencanakan.id.talentpool.dto.RecommendationResponseDTO;
import rencanakan.id.talentpool.enums.StatusType;
import rencanakan.id.talentpool.mapper.DTOMapper;
import rencanakan.id.talentpool.model.Recommendation;
import rencanakan.id.talentpool.model.User;
import rencanakan.id.talentpool.repository.RecommendationRepository;
import rencanakan.id.talentpool.repository.UserRepository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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
        
        RecommendationResponseDTO dto = DTOMapper.map(recommendation, RecommendationResponseDTO.class);
        dto.setTalentId(recommendation.getTalent().getId());
        return dto;
    }
    
    @Override
    public List<RecommendationResponseDTO> getByTalentId(String talentId) {
        User talent = userRepository.findById(talentId)
                .orElseThrow(() -> new EntityNotFoundException("User not found with id: " + talentId));

        List<Recommendation> recommendations = recommendationRepository.findByTalent(talent);
        
        return recommendations.stream()
                .map(recommendation -> DTOMapper.map(recommendation, RecommendationResponseDTO.class))
                .collect(Collectors.toList());
    }

    @Override
    public List<RecommendationResponseDTO> getByTalentIdAndStatus(String talentId, StatusType status) {
        User talent = userRepository.findById(talentId)
                .orElseThrow(() -> new EntityNotFoundException("User not found with id: " + talentId));

        List<Recommendation> recommendations = recommendationRepository.findByTalentAndStatus(talent, status);
        
        return recommendations.stream()
                .map(recommendation -> DTOMapper.map(recommendation, RecommendationResponseDTO.class))
                .collect(Collectors.toList());
    }
    
    @Override
    public Map<StatusType, List<RecommendationResponseDTO>> getByTalentIdAndGroupedByStatus(String talentId) {
        Map<StatusType, List<RecommendationResponseDTO>> groupedRecommendations = new HashMap<>();
        
        for (StatusType status : StatusType.values()) {
            groupedRecommendations.put(status, getByTalentIdAndStatus(talentId, status));
        }
        
        return groupedRecommendations;
    }
}
