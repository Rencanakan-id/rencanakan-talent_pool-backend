package rencanakan.id.talentPool.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter @Setter
@NoArgsConstructor

public class UserProfile {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;
    private String firstName;
    private String lastName;
    private String phoneNumber;
    private String domisili;
    private String availLocation;
    private String occupation;

    public UserProfile(String id, String firstName, String lastName, String phoneNumber, String domisili, String availLocation, String occupation) {

    }
}
