package rencanakan.id.talentpool.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CertificateRequestDTO {
    @NotBlank(message = "Title is required")
    @Size(max = 50, message = "Title must not exceed 50 characters")
    private String title;

    @NotBlank(message = "File URL is required")
    @Pattern(regexp = ".*\\.(pdf|jpg|png)$", message = "Only PDF, JPG, or PNG files are allowed")
    private String file;
}