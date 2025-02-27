package rencanakan.id.talentPool.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Setter
@Getter
public class ExperienceDTO {

    // Getter and Setter methods
    private String id;
    private String title;
    private String company;
    private Date startDate;
    private Date endDate;
    private String photoUrl;
    private String talentId;  // Only the Talent ID is needed, not the full Talent object

    // Constructors
    public ExperienceDTO() {}

    public ExperienceDTO(String id, String title, String company, Date startDate, Date endDate, String photoUrl, String talentId) {
        this.id = id;
        this.title = title;
        this.company = company;
        this.startDate = startDate;
        this.endDate = endDate;
        this.photoUrl = photoUrl;
        this.talentId = talentId;
    }

}
