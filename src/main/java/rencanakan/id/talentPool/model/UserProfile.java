package rencanakan.id.talentPool.model;

import jakarta.persistence.*;
import lombok.*;

import java.util.List;
import java.util.UUID;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(exclude = "id")
@Entity
@Table(name = "user_profiles")
public class UserProfile {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @Column(name = "first_name", length = 32, nullable = false)
    private String firstName;

    @Column(name = "last_name", length = 32, nullable = false)
    private String lastName;

    @Column(name = "email", length = 255, unique = true, nullable = false)
    private String email;

    @Column(name = "password", nullable = false)
    private String password;

    @Column(name = "phone", length = 32, unique = true, nullable = false)
    private String phoneNumber;

    @Column(name = "address", length = 255)
    private String address;

    @Column(name = "job", length = 64)
    private String job;

    @Column(name = "photo", length = 255)
    private String photo;

    @Column(name = "about_me", columnDefinition = "TEXT")
    private String aboutMe;

    @Column(name = "nik", length = 16, unique = true, nullable = false)
    private String nik;

    @Column(name = "npwp", length = 20, unique = true)
    private String npwp;

    @Column(name = "photo_ktp", length = 255)
    private String photoKtp;

    @Column(name = "photo_npwp", length = 255)
    private String photoNpwp;

    @Column(name = "photo_ijazah", length = 255)
    private String photoIjazah;

    @Column(name = "experience_years")
    private Integer experienceYears;

    @Column(name = "skk_level", length = 64)
    private String skkLevel;

    @Column(name = "current_location", length = 255)
    private String currentLocation;

    @ElementCollection
    @CollectionTable(name = "user_preferred_locations", joinColumns = @JoinColumn(name = "user_id"))
    @Column(name = "preferred_location")
    private List<String> preferredLocations;

    @ElementCollection
    @CollectionTable(name = "user_skills", joinColumns = @JoinColumn(name = "user_id"))
    @Column(name = "skill")
    private List<String> skills;

    // Setter khusus untuk menghindari null UUID saat menggunakan Builder
    public void setId(String id) {
        this.id = (id == null) ? UUID.randomUUID().toString() : id;
    }
}
