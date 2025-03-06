package rencanakan.id.talentpool.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import rencanakan.id.talentpool.model.Experience;

@Repository
public interface ExperienceRepository extends CrudRepository<Experience, Long> {

}
