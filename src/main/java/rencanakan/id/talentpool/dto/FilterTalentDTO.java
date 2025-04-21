package rencanakan.id.talentpool.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Builder
@Getter
@Setter
public class FilterTalentDTO {
    String name;
    List<String> skills;
    List<String> preferredLocations;
    List<Double> priceRange;
}
