package rencanakan.id.talentPool.model;

import rencanakan.id.talentPool.enums.EmploymentType;
import rencanakan.id.talentPool.enums.LocationType;

import java.time.LocalDate;

public class ExperienceBuilder {
    private String title;
    private String company;
    private EmploymentType employmentType;
    private LocalDate startDate;
    private LocalDate endDate;
    private String location;
    private LocationType locationType;
    private long talentId;

    public ExperienceBuilder setTitle(String title) {
        this.title = title;
        return this;
    }

    public ExperienceBuilder setCompany(String company) {
        this.company = company;
        return this;
    }

    public ExperienceBuilder setEmploymentType(EmploymentType employmentType) {
        this.employmentType = employmentType;
        return this;
    }

    public ExperienceBuilder setStartDate(LocalDate startDate) {
        this.startDate = startDate;
        return this;
    }

    public ExperienceBuilder setEndDate(LocalDate endDate) {
        this.endDate = endDate;
        return this;
    }

    public ExperienceBuilder setLocation(String location) {
        this.location = location;
        return this;
    }

    public ExperienceBuilder setLocationType(LocationType locationType) {
        this.locationType = locationType;
        return this;
    }

    public ExperienceBuilder setTalentId(long talentId) {
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