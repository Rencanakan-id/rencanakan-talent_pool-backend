package rencanakan.id.talentpool.specification;

import org.springframework.data.jpa.domain.Specification;
import rencanakan.id.talentpool.model.User;
import java.util.List;
import java.util.stream.Collectors;

public class LocationSpecification {
    private LocationSpecification() {
        // Private constructor to prevent instantiation
    }

    public static Specification<User> hasPreferredLocations(List<String> locations) {
        return (root, query, builder) -> {
            if (locations == null || locations.isEmpty()) return null;
            List<jakarta.persistence.criteria.Predicate> predicates = locations.stream()
                .map(location -> builder.equal(
                    builder.lower(root.get("currentLocation")),
                    location.toLowerCase()
                ))
                .toList();
            return builder.or(predicates.toArray(new jakarta.persistence.criteria.Predicate[0]));
        };
    }
}
