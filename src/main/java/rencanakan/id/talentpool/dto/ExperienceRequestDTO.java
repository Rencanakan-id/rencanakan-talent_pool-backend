package rencanakan.id.talentpool.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.*;
import rencanakan.id.talentpool.enums.EmploymentType;
import rencanakan.id.talentpool.enums.LocationType;

import java.time.LocalDate;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ExperienceRequestDTO {
    @NotBlank(message = "Title is required")
    @Size(max = 50, message = "Title must not exceed 50 characters")
    private String title;

    @NotBlank(message = "Company is required")
    @Size(max = 50, message = "Company must not exceed 50 characters")
    private String company;

    @NotNull(message = "Employment type is required")
    private EmploymentType employmentType;

    @NotNull(message = "Start date is required")
    private LocalDate startDate;

    private LocalDate endDate;

    @NotBlank(message = "Location is required")
    @Size(max = 50, message = "Location must not exceed 50 characters")
    private String location;

    @NotNull(message = "Location type is required")
    private LocationType locationType;
}
