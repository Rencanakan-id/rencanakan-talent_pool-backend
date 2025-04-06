package rencanakan.id.talentpool.dto;

import lombok.*;

import java.util.List;

@NoArgsConstructor
@Getter
@Setter
@Builder
@AllArgsConstructor
public class UserResponseDTO {
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
