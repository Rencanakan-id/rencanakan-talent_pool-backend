package rencanakan.id.talentpool.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;

@Entity
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "certificate")
public class Certificate {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 50)
    @NotBlank(message = "Title is required")
    @Size(max = 50, message = "Title must not exceed 50 characters")
    private String title;

    @Column(nullable = false)
    @NotBlank(message = "File URL is required")
    @Pattern(regexp = ".*\\.(pdf|jpg|png)$", message = "Only PDF, JPG, or PNG files are allowed")
    private String file;

    @ManyToOne
    @JoinColumn(name = "talent_id", nullable = false)
    @NotNull(message = "Talent (user) is required")
    private User user;
}
