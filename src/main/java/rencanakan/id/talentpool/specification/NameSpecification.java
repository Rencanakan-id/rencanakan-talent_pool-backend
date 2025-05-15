package rencanakan.id.talentpool.specification;

import org.springframework.data.jpa.domain.Specification;
import rencanakan.id.talentpool.model.User;

public class NameSpecification {
    private NameSpecification() {
        // Private constructor to prevent instantiation
    }

    public static Specification<User> hasName(String name) {
        return (root, query, builder) -> {
            if (name == null || name.trim().isEmpty()) return null;
            String keyword = "%" + name.toLowerCase() + "%";
            return builder.or(
                builder.like(builder.lower(root.get("firstName")), keyword),
                builder.like(builder.lower(root.get("lastName")), keyword),
                builder.like(
                    builder.lower(
                        builder.concat(
                            builder.concat(root.get("firstName"), " "),
                            root.get("lastName")
                        )
                    ),
                    keyword
                )
            );
        };
    }
}
