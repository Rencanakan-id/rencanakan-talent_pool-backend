package rencanakan.id.talentPool.dto;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class WebResponse <T> {

    private T data;

    private String errors;
}
