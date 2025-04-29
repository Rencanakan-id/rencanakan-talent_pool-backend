package rencanakan.id.talentpool.dto;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class WebResponse <T> {

    private T data;
    private int page;
    private int size;
    private int totalPages;
    private String errors;
}
