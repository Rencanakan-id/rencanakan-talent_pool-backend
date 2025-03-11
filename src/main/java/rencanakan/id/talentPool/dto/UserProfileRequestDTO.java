package rencanakan.id.talentPool.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class UserProfileRequestDTO {

    @NotBlank(message = "First name is required")
    private String firstName;

    @NotBlank(message = "Last name is required")
    private String lastName;

    @NotBlank(message = "Email is required")
    private String email;

    @NotBlank(message = "Phone number is required")
    private String phoneNumber;

    @NotBlank(message = "Address is required")
    private String address;

    @NotBlank(message = "Job is required")
    private String job;

    private String photo;

    @NotBlank(message = "About me is required")
    private String aboutMe;

    @NotBlank(message = "NIK is required")
    private String nik;

    @NotBlank(message = "NPWP is required")
    private String npwp;

    private String photoKtp;

    private String photoNpwp;

    private String photoIjazah;

    @NotNull(message = "Years of experience is required")
    private Integer experienceYears;

    @NotBlank(message = "SKK level is required")
    private String skkLevel;

    @NotBlank(message = "Current location is required")
    private String currentLocation;

    @NotNull(message = "Preferred location is required")
    private List<String> preferredLocations;

    @NotBlank(message = "Skill is required")
    private String skill;
}
