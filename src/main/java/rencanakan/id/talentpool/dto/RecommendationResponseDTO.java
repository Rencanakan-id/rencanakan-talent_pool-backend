package rencanakan.id.talentpool.dto;

import lombok.*;
import rencanakan.id.talentpool.enums.StatusType;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RecommendationResponseDTO {
    private String id;
    private String talentId;
    private Long contractorId;
    private String contractorName;
    private String message;
    private StatusType status;
}