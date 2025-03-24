package rencanakan.id.talentpool.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import rencanakan.id.talentpool.model.Experience;
import rencanakan.id.talentpool.model.Recommendation;

@Repository
public interface RecommendationRepository extends JpaRepository<Recommendation, Long> {
}
