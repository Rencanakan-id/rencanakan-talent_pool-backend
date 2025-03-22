package rencanakan.id.talentpool.controller;

import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import rencanakan.id.talentpool.dto.LoginRequestDTO;
import rencanakan.id.talentpool.dto.LoginResponseDTO;
import rencanakan.id.talentpool.dto.UserRequestDTO;
import rencanakan.id.talentpool.dto.WebResponse;
import rencanakan.id.talentpool.model.User;
import rencanakan.id.talentpool.service.AuthenticationService;
import rencanakan.id.talentpool.service.JwtService;

@RestController
@RequestMapping("/auth")
public class AuthenticationController {

    private final JwtService jwtService;
    private final AuthenticationService authenticationService;

    public AuthenticationController(JwtService jwtService, AuthenticationService authenticationService) {
        this.jwtService = jwtService;
        this.authenticationService = authenticationService;
    }

    @PostMapping("/register")
    public ResponseEntity<WebResponse<User>> register(@Valid @RequestBody UserRequestDTO registerUserDto) {
        User registeredUser = authenticationService.signup(registerUserDto);
        WebResponse<User> response = WebResponse.<User>builder()
                .data(registeredUser)
                .errors(null)
                .build();

        return ResponseEntity.ok(response);
    }

    @PostMapping("/login")
    public ResponseEntity<WebResponse<LoginResponseDTO>> authenticate(@RequestBody LoginRequestDTO loginUserDto) {
        User authenticatedUser = authenticationService.authenticate(loginUserDto);
        String jwtToken = jwtService.generateToken(authenticatedUser);

        LoginResponseDTO loginResponse = new LoginResponseDTO();
        loginResponse.setToken(jwtToken);
        loginResponse.setExpiresIn(jwtService.getExpirationTime());

        WebResponse<LoginResponseDTO> response = WebResponse.<LoginResponseDTO>builder()
                .data(loginResponse)
                .errors(null)
                .build();

        return ResponseEntity.ok(response);
    }
}