package rencanakan.id.talentpool.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
public class LoginResponseDTO {
    private String token;
    private long expiresIn;

    public LoginResponseDTO setToken(String token) {
        this.token = token;
        return this;
    }

    public LoginResponseDTO setExpiresIn(long expiresIn) {
        this.expiresIn = expiresIn;
        return this;
    }
}
