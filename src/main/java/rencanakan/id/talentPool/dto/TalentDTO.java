package rencanakan.id.talentPool.dto;

import lombok.Getter;
import lombok.Setter;
import java.util.List;

@Getter
@Setter
public class TalentDTO {
    private String id;
    private String firstName;
    private String lastName;
    private String email;
    private String phoneNumber;
    private String jobTitle;
    private String location;
    private String domicile;
    // Jika perlu, tambahkan daftar experience
    private List<ExperienceDTO> experiences;
}
