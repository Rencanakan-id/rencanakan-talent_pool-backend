package rencanakan.id.talentPool.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import rencanakan.id.talentPool.model.Experience;

import java.util.List;

public interface ExperienceRepository extends JpaRepository<Experience,Long>, JpaSpecificationExecutor<Experience> {
    List<Experience> findByTalentId(Long id);
}
