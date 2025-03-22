package rencanakan.id.talentpool.service;

import jakarta.validation.Valid;
import jakarta.validation.Validator;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import rencanakan.id.talentpool.dto.LoginUserDTO;
import rencanakan.id.talentpool.dto.UserRequestDTO;
import rencanakan.id.talentpool.model.User;
import rencanakan.id.talentpool.repository.UserRepository;

@Service
public class AuthenticationService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;

    public AuthenticationService(
            UserRepository userRepository,
            AuthenticationManager authenticationManager,
            PasswordEncoder passwordEncoder, Validator validator
    ) {
        this.authenticationManager = authenticationManager;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public User signup(@Valid UserRequestDTO request) {
        User newUser = User.builder()
                .firstName(request.getFirstName())
                .password(passwordEncoder.encode(request.getPassword()))
                .lastName(request.getLastName())
                .email(request.getEmail())
                .phoneNumber(request.getPhoneNumber())
                .photo(request.getPhoto())
                .aboutMe(request.getAboutMe())
                .nik(request.getNik())
                .npwp(request.getNpwp())
                .photoKtp(request.getPhotoKtp())
                .photoNpwp(request.getPhotoNpwp())
                .photoIjazah(request.getPhotoIjazah())
                .experienceYears(request.getExperienceYears())
                .skkLevel(request.getSkkLevel())
                .currentLocation(request.getCurrentLocation())
                .preferredLocations(request.getPreferredLocations())
                .skill(request.getSkill())
                .price(request.getPrice())
                .build();
        return userRepository.save(newUser);
    }

    public User authenticate(LoginUserDTO input) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        input.getEmail(),
                        input.getPassword()
                )
        );

        return userRepository.findByEmail(input.getEmail()).orElseThrow();
    }
}