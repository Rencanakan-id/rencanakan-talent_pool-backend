package rencanakan.id.talentpool.specification;

import org.springframework.data.jpa.domain.Specification;
import rencanakan.id.talentpool.model.User;
import java.util.List;

public class PriceSpecification {
    private PriceSpecification() {
        // Private constructor to prevent instantiation
    }

    public static Specification<User> inPriceRange(List<Double> priceRange) {
        return (root, query, builder) -> {
            if (priceRange == null || priceRange.size() != 2) return null;
            Double minPrice = priceRange.get(0);
            Double maxPrice = priceRange.get(1);
            return builder.between(root.get("price"), minPrice, maxPrice);
        };
    }
}
