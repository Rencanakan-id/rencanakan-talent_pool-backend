package rencanakan.id.talentpool.controller;

import jakarta.validation.Valid;
import org.apache.coyote.BadRequestException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import rencanakan.id.talentpool.dto.*;
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
    public ResponseEntity<WebResponse<User>> register(@Valid @RequestBody UserRequestDTO registerUserDto) throws BadRequestException {
        User registeredUser;
        try {
            registeredUser = authenticationService.signup(registerUserDto);

        } catch (Exception e) {
            WebResponse<User> response = WebResponse.<User>builder()
                    .data(null)
                    .errors(e.getMessage())
                    .build();
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }
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

    @PatchMapping("/password")
    public ResponseEntity<String> resetPassword(@RequestBody ResetPasswordDTO request) {
        authenticationService.resetPasswordWithToken(request.getToken(), request.getNewPassword());
        return ResponseEntity.ok("Password successfully reset.");
    }
}