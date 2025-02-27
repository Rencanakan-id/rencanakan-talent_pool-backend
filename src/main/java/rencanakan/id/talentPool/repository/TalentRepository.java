package rencanakan.id.talentPool.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import rencanakan.id.talentPool.model.Talent;

@Repository
public interface TalentRepository extends JpaRepository<Talent, String> {
}
