package rencanakan.id.talentPool.model;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.List;

@Setter
@Getter
@Entity
public class Talent {

    // Getter dan Setter
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private String id;

    private String firstName;
    private String lastName;
    private String email;
    private String phoneNumber;
    private String jobTitle;
    private String location;
    private String domicile;

    @OneToMany(mappedBy = "talent", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Experience> experiences; // Relasi ke pengalaman

}
