package rencanakan.id.talentpool.dto;

import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.validation.constraints.NotNull;

import lombok.*;
import rencanakan.id.talentpool.enums.StatusType;

@Getter
@Setter
@AllArgsConstructor
public class RecommendationStatusRequestDTO {

    @NotNull(message = "Status is required")
    private StatusType status;


}
