package rencanakan.id.talentpool.dto;

import lombok.*;
import rencanakan.id.talentpool.enums.StatusType;
import rencanakan.id.talentpool.model.User;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RecommendationResponseDTO {
    private String id;
    private User talent;
    private Long contractorId;
    private String contractorName;
    private String message;
    private StatusType status;
}