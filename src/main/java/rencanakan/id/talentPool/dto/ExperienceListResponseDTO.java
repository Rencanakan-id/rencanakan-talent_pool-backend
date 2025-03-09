package rencanakan.id.talentPool.dto;

import java.util.List;
import lombok.*;

@NoArgsConstructor
@Getter
@Setter
@AllArgsConstructor
public class ExperienceListResponseDTO {
    private List<ExperienceResponseDTO> experiences;
}