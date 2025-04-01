package rencanakan.id.talentpool.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;
import rencanakan.id.talentpool.enums.StatusType;

@Entity
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "recommendation")
public class Recommendation {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @ManyToOne
    @JoinColumn(name = "talent_id", nullable = false)
    @NotNull(message = "Talent (User) is required")
    private User talent;

    @Column(name = "contractor_id", nullable = false)
    @NotNull(message = "Contractor ID is required")
    private Long contractorId;

    @Column(name = "contractor_name", nullable = false)
    @NotBlank(message = "Contractor name is required")
    private String contractorName;

    @Column(name = "message", nullable = false, columnDefinition = "TEXT")
    @NotBlank(message = "Message is required")
    @Size(max = 4000, message = "Message cannot exceed 4000 characters")
    private String message;

    @Enumerated(EnumType.STRING)
    @Column(name = "status_type", nullable = false)
    @NotNull(message = "Status is required")
    private StatusType status;

    public static RecommendationBuilder builder() {
        return new RecommendationBuilder();
    }
}
