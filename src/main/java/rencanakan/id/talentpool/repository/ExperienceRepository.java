package rencanakan.id.talentpool.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import rencanakan.id.talentpool.model.Experience;

public interface ExperienceRepository extends JpaRepository<Experience,Long>, JpaSpecificationExecutor<Experience> {
}
