package rencanakan.id.talentpool.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import rencanakan.id.talentpool.model.Experience;
import rencanakan.id.talentpool.model.Recommendation;

public interface RecommendationRepository extends JpaRepository<Recommendation, String> {
}
