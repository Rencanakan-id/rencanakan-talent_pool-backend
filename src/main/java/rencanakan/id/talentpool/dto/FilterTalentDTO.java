package rencanakan.id.talentpool.dto;

import lombok.*;

import java.util.List;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class FilterTalentDTO {
    String name;
    List<String> skills;
    List<String> preferredLocations;
    List<Double> priceRange;
}
