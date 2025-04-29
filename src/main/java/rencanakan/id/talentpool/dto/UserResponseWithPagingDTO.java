package rencanakan.id.talentpool.dto;

import lombok.*;

import java.util.List;
@NoArgsConstructor
@Getter
@Setter
@Builder
@AllArgsConstructor
public class UserResponseWithPagingDTO {
    private List<UserResponseDTO> users;
    private int page;
    private int size;
    private int totalPages;

}
