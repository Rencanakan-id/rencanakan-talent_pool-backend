package rencanakan.id.talentPool.dto;

import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;
import rencanakan.id.talentPool.enums.EmploymentType;
import rencanakan.id.talentPool.enums.LocationType;


import java.time.LocalDate;

@Getter
@Setter
public class EditExperienceRequestDTO {

    @NotBlank(message = "Please provide a job title.")
    @Size(max = 50, message = "Title can't exceed 50 characters.")
    private String title;

    @NotBlank(message = "Please provide the company name.")
    @Size(max = 50, message = "Company name can't exceed 50 characters.")
    private String company;

    @NotNull(message = "Employment type is required.")
    private EmploymentType employmentType;

    @NotNull(message = "Start date is required.")
    private LocalDate startDate;

    private LocalDate endDate;

    @NotBlank(message = "Please provide the job location.")
    @Size(max = 50, message = "Location can't exceed 50 characters.")
    private String location;

    @NotNull(message = "Location type is required.")
    private LocationType locationType;

    @Positive(message = "Talent ID must be a positive number.")
    private long talentId;

    @AssertTrue(message = "End date must not be earlier than start date")
    public boolean isEndDateValid() {
        if (endDate == null) {
            return true;
        }
        return !endDate.isBefore(startDate);
    }
}
