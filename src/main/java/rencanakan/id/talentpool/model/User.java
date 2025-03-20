package rencanakan.id.talentpool.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Size;
import lombok.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "users")
public class User implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @Size(max = 32, message = "First name exceeds maximum length")
    @Column(name = "first_name", length = 32, nullable = false)
    private String firstName;

    @Column(name = "last_name", length = 32, nullable = false)
    private String lastName;

    @Email (message = "Invalid email format")
    @Column(name = "email", length = 255, unique = true, nullable = false)
    private String email;

    @Size(min = 8, message = "Password must be at least 8 characters")
    @Column(name = "password", nullable = false)
    private String password;

    @Column(name = "phone", length = 32, unique = true, nullable = false)
    private String phoneNumber;

    @Column(name = "photo", length = 255)
    private String photo;

    @Column(name = "about_me", columnDefinition = "TEXT")
    private String aboutMe;

    @Size(min = 16, max = 16, message = "NIK must be exactly 16 digits")
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

    @Column(name = "skill", length = 255)
    private String skill;

    @Column(name = "price")
    private Integer price;

    public void setId(String id) {
        this.id = (id == null) ? UUID.randomUUID().toString() : id;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of();
    }

    @Override
    public String getUsername() {
        return this.email;
    }
}
