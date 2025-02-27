package rencanakan.id.talentPool.model;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.Date;

@Setter
@Getter
@Entity
public class Experience {

    // Getter dan Setter
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private String id;

    private String title;
    private String company;
    private Date startDate;
    private Date endDate;

    private String photoUrl;

    @ManyToOne
    @JoinColumn(name = "talent_id")
    private Talent talent; // Relasi dengan Talent

}
