package rencanakan.id.talentpool.dto;

import lombok.*;
import rencanakan.id.talentpool.enums.StatusType;

@Getter
@Setter
@Builder
@AllArgsConstructor
public class RecommendationRequestDTO {
    private Long contractorId;
    private String contractorName;
    private String message;
    private StatusType status;

    public RecommendationRequestDTO() { }
}
