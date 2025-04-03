package rencanakan.id.talentpool.service;

import rencanakan.id.talentpool.dto.*;
import rencanakan.id.talentpool.enums.StatusType;

public interface RecommendationService {
    RecommendationResponseDTO editStatusById(String id, StatusType status);
}
