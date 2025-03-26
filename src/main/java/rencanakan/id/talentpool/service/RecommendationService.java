package rencanakan.id.talentpool.service;

import rencanakan.id.talentpool.dto.*;

public interface RecommendationService {
    RecommendationResponseDTO editStatusById(String id, RecommendationStatusRequestDTO dto);
    RecommendationResponseDTO deleteById(String id);
}
