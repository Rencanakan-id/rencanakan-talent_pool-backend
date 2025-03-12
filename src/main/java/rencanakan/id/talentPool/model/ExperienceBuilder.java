package rencanakan.id.talentpool.model;

import java.time.LocalDate;

import rencanakan.id.talentpool.enums.EmploymentType;
import rencanakan.id.talentpool.enums.LocationType;

public class ExperienceBuilder {
    private String title;
    private String company;
    private EmploymentType employmentType;
    private LocalDate startDate;
    private LocalDate endDate;
    private String location;
    private LocationType locationType;
    private Long talentId;

    public ExperienceBuilder title(String title) {
        this.title = title;
        return this;
    }

    public ExperienceBuilder company(String company) {
        this.company = company;
        return this;
    }

    public ExperienceBuilder employmentType(EmploymentType employmentType) {
        this.employmentType = employmentType;
        return this;
    }

    public ExperienceBuilder startDate(LocalDate startDate) {
        this.startDate = startDate;
        return this;
    }

    public ExperienceBuilder endDate(LocalDate endDate) {
        this.endDate = endDate;
        return this;
    }

    public ExperienceBuilder location(String location) {
        this.location = location;
        return this;
    }

    public ExperienceBuilder locationType(LocationType locationType) {
        this.locationType = locationType;
        return this;
    }

    public ExperienceBuilder talentId(Long talentId) {
        this.talentId = talentId;
        return this;
    }

    public Experience build() {
        Experience experience = new Experience();
        experience.setTitle(title);
        experience.setCompany(company);
        experience.setEmploymentType(employmentType);
        experience.setStartDate(startDate);
        experience.setEndDate(endDate);
        experience.setLocation(location);
        experience.setLocationType(locationType);
        experience.setTalentId(talentId);

        return experience;
    }
}