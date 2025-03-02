package rencanakan.id.talentPool.dto;

import rencanakan.id.talentPool.enums.EmploymentType;
import rencanakan.id.talentPool.enums.LocationType;

import java.time.LocalDate;
import lombok.*;

@NoArgsConstructor
@Getter
@Setter
@AllArgsConstructor
public class ExperienceResponseDTO {
    private Long id;
    private String title;
    private String company;
    private EmploymentType employmentType;
    private LocalDate startDate;
    private LocalDate endDate;
    private String location;
    private LocationType locationType;
    private Long talentId;
}
