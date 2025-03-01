package rencanakan.id.talentPool.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;
import rencanakan.id.talentPool.enums.EmploymentType;
import rencanakan.id.talentPool.enums.LocationType;

import java.time.LocalDate;
import java.util.Date;

@Entity
@Getter
@Setter
@Table(name = "experience")
public class Experience {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(nullable = false, length = 50)
    @NotBlank(message = "Title is required")
    @Size(max = 50, message = "Title must not exceed 50 characters")
    private String title;

    @Column(nullable = false, length = 50)
    @NotBlank(message = "Company is required")
    @Size(max = 50, message = "Company must not exceed 50 characters")
    private String company;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, name = "employment_type")
    @NotNull(message = "Employment type is required")
    private EmploymentType employmentType;

    @Column(nullable = false, name = "start_date")
    @NotNull(message = "Start date is required")
    private LocalDate startDate;

    @Column(name = "end_date")
    private LocalDate endDate;

    @Column(nullable = false, length = 50)
    @NotBlank(message = "Location is required")
    @Size(max = 50, message = "Location must not exceed 50 characters")
    private String location;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, name = "location_type")
    @NotNull(message = "Location type is required")
    private LocationType locationType;

    @Column(nullable = false, name = "talent_id")
    @Positive(message = "Talent ID must be a positive number")
    private long talentId;

    public static ExperienceBuilder builder() {
        return new ExperienceBuilder();
    }

    @AssertTrue(message = "End date must not be earlier than start date")
    public boolean isEndDateAfterStartDate() {
        if (endDate == null) {
            return true;
        }
        return startDate != null && !endDate.isBefore(startDate);
    }
}
