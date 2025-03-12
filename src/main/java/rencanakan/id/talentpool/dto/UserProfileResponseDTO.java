package rencanakan.id.talentpool.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@NoArgsConstructor
@Getter
@Setter
@AllArgsConstructor
public class UserProfileResponseDTO {
    private String id;
    private String firstName;
    private String lastName;
    private String email;
    private String phoneNumber;
    private String photo;
    private String aboutMe;
    private String nik;
    private String npwp;
    private String photoKtp;
    private String photoNpwp;
    private String photoIjazah;
    private Integer experienceYears;
    private String skkLevel;
    private String currentLocation;
    private List<String> preferredLocations;
    private String skill;
    private Integer price;
}
