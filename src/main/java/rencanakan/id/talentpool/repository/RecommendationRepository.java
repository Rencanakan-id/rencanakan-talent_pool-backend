package rencanakan.id.talentpool.repository;

import rencanakan.id.talentpool.model.Experience;

import java.util.List;

public interface RecommendationRepository {
    List<Experience> findByUserId(String user_id);
}
