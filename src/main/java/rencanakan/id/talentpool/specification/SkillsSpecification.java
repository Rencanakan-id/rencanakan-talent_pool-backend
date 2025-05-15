package rencanakan.id.talentpool.specification;

import org.springframework.data.jpa.domain.Specification;
import rencanakan.id.talentpool.model.User;
import java.util.List;

public class SkillsSpecification {
    private SkillsSpecification() {
        // Private constructor to prevent instantiation
    }

    public static Specification<User> hasSkills(List<String> skills) {
        return (root, query, builder) -> {
            if (skills == null || skills.isEmpty()) return null;
            List<jakarta.persistence.criteria.Predicate> predicates = skills.stream()
                .map(skill -> builder.equal(
                    builder.lower(root.get("skill")),
                    skill.toLowerCase()
                ))
                .toList();
            return builder.or(predicates.toArray(new jakarta.persistence.criteria.Predicate[0]));
        };
    }
}
