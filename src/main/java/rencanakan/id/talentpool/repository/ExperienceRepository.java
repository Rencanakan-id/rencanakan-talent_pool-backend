package rencanakan.id.talentpool.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import rencanakan.id.talentpool.model.Experience;

@Repository
public interface ExperienceRepository extends JpaRepository<Experience, Long> {
}
