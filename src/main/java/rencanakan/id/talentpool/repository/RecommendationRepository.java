package rencanakan.id.talentpool.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import rencanakan.id.talentpool.model.Experience;

import java.util.List;

@Repository
public interface RecommendationRepository JpaRepository<Recommendation, Long> {
    List<Experience> findByUserId(String user_id);
}
