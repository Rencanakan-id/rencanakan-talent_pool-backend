package rencanakan.id.talentPool.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import rencanakan.id.talentPool.model.Experience;

import java.util.List;

@Repository
public interface ExperienceRepository extends JpaRepository<Experience, String> {

    List<Experience> findByTalentId(String talentId);
}
