package rencanakan.id.talentpool.dto;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CertificateResponseDTO {
    private Long id;
    private String title;
    private String file;
    private String talentId;
}
