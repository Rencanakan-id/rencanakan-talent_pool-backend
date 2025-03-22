package rencanakan.id.talentpool.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import rencanakan.id.talentpool.dto.LoginResponseDTO;
import rencanakan.id.talentpool.dto.LoginUserDTO;
import rencanakan.id.talentpool.dto.UserRequestDTO;
import rencanakan.id.talentpool.model.User;
import rencanakan.id.talentpool.service.AuthenticationService;
import rencanakan.id.talentpool.service.JwtService;


@RestController
@RequestMapping("/auth")
public class AuthenticationController {

    private final JwtService jwtService;
    private final AuthenticationService authenticationService;

    @Autowired
    public AuthenticationController(JwtService jwtService, AuthenticationService authenticationService) {
        this.jwtService = jwtService;
        this.authenticationService = authenticationService;
    }

    @PostMapping("/register")
    public ResponseEntity<User> register(@RequestBody UserRequestDTO registerUserDto) {
        User registeredUser = authenticationService.signup(registerUserDto);
        return ResponseEntity.ok(registeredUser);
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponseDTO> authenticate(@RequestBody LoginUserDTO loginUserDto) {
        UserDetails userDetails = authenticationService.authenticate(loginUserDto);
        String jwtToken = jwtService.generateToken(userDetails);
        LoginResponseDTO loginResponse = new LoginResponseDTO().setToken(jwtToken).setExpiresIn(jwtService.getExpirationTime());
        return ResponseEntity.ok(loginResponse);
    }

    @GetMapping()
    public String hi() {
        return "hii";
    }
}