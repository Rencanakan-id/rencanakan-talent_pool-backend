package rencanakan.id.talentPool.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import rencanakan.id.talentPool.model.Experience;

public interface ExperienceRepository extends JpaRepository<Experience,Long>, JpaSpecificationExecutor<Experience> {
}
