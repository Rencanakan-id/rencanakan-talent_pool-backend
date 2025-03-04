package rencanakan.id.talentPool.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import rencanakan.id.talentPool.model.Experience;

@Repository
public interface ExperienceRepository extends CrudRepository<Experience, Long> {

}
