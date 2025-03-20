package rencanakan.id.talentpool.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;
import rencanakan.id.talentpool.enums.StatusType;

@Getter
@Setter
@Builder
@AllArgsConstructor
public class RecommendationRequestDTO {
    @NotNull(message = "Contractor ID is required")
    private Long contractorId;
    
    @NotBlank(message = "Contractor name is required")
    private String contractorName;

    @NotBlank(message = "Message is required")
    @Size(max = 4000, message = "Message cannot exceed 4000 characters")
    private String message;

    @NotNull(message = "Status is required")
    private StatusType status;

    public RecommendationRequestDTO() { }
}