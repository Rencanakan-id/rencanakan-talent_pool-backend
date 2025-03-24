package rencanakan.id.talentpool.service;

import org.springframework.stereotype.Service;
import rencanakan.id.talentpool.dto.RecommendationResponseDTO;
import rencanakan.id.talentpool.enums.StatusType;
import rencanakan.id.talentpool.repository.RecommendationRepository;
import rencanakan.id.talentpool.repository.UserProfileRepository;

import java.util.List;
import java.util.Map;

@Service
public class RecommendationServiceImpl implements RecommendationService {
    
    private final RecommendationRepository recommendationRepository;
    private final UserProfileRepository userRepository;

    public RecommendationServiceImpl(RecommendationRepository recommendationRepository, UserProfileRepository userRepository) {
        this.recommendationRepository = recommendationRepository;
        this.userRepository = userRepository;
    }
    
    @Override
    public RecommendationResponseDTO getById(String id) {
        return null;
    }
    
    @Override
    public List<RecommendationResponseDTO> getAll() {
        return null;
    }
    
    @Override
    public List<RecommendationResponseDTO> getByStatus(StatusType status) {
        return null;
    }
    
    @Override
    public Map<StatusType, List<RecommendationResponseDTO>> getAllGroupedByStatus() {
        return null;
    }
    
    @Override
    public List<RecommendationResponseDTO> getByTalentId(String talentId) {
        return null;
    }

    @Override
    public List<RecommendationResponseDTO> getByTalentIdAndStatus(String talentId, StatusType status) {
        return null;
    }
    
    @Override
    public Map<StatusType, List<RecommendationResponseDTO>> getByTalentIdAndGroupedByStatus(String talentId) {
        return null;
    }
}
