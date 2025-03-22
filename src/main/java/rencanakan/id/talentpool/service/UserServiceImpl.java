package rencanakan.id.talentpool.service;

import jakarta.validation.Validator;
import org.springframework.stereotype.Service;

import rencanakan.id.talentpool.dto.UserResponseDTO;
import rencanakan.id.talentpool.model.User;
import rencanakan.id.talentpool.repository.UserRepository;

import java.util.Optional;

@Service
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final Validator validator;
    private final JwtService jwtService;

    public UserServiceImpl(UserRepository userRepository, Validator validator, JwtService jwtService) {
        this.userRepository = userRepository;
        this.validator = validator;
        this.jwtService = jwtService;
    }

    @Override
    public UserResponseDTO getById(String id) {
        Optional<User> userProfileOptional = userRepository.findById(id);
        
        if (userProfileOptional.isPresent()) {
            User user = userProfileOptional.get();
            return convertToDTO(user);
        }
        
        return null;
    }

    @Override
    public UserResponseDTO editProfile(String id, User editedProfile) {
        Optional<User> userProfileOptional = userRepository.findById(id);

        if (userProfileOptional.isPresent()) {
            User user = userProfileOptional.get();

            try {
                if (editedProfile.getEmail() != null) {
                    user.setEmail(editedProfile.getEmail());
                }
                if (editedProfile.getPassword() != null) {
                    user.setPassword(editedProfile.getPassword());
                }
                if (editedProfile.getNik() != null) {
                    user.setNik(editedProfile.getNik());
                }

                if (editedProfile.getFirstName() != null) {
                    user.setFirstName(editedProfile.getFirstName());
                }
                if (editedProfile.getLastName() != null) {
                    user.setLastName(editedProfile.getLastName());
                }
                if (editedProfile.getEmail() != null) {
                    user.setEmail(editedProfile.getEmail());
                }
                if (editedProfile.getPassword() != null) {
                    user.setPassword(editedProfile.getPassword());
                }
                if (editedProfile.getPhoneNumber() != null) {
                    user.setPhoneNumber(editedProfile.getPhoneNumber());
                }
                if (editedProfile.getPhoto() != null) {
                    user.setPhoto(editedProfile.getPhoto());
                }
                if (editedProfile.getAboutMe() != null) {
                    user.setAboutMe(editedProfile.getAboutMe());
                }
                if (editedProfile.getNik() != null) {
                    user.setNik(editedProfile.getNik());
                }
                if (editedProfile.getNpwp() != null) {
                    user.setNpwp(editedProfile.getNpwp());
                }
                if (editedProfile.getPhotoKtp() != null) {
                    user.setPhotoKtp(editedProfile.getPhotoKtp());
                }
                if (editedProfile.getPhotoNpwp() != null) {
                    user.setPhotoNpwp(editedProfile.getPhotoNpwp());
                }
                if (editedProfile.getPhotoIjazah() != null) {
                    user.setPhotoIjazah(editedProfile.getPhotoIjazah());
                }
                if (editedProfile.getExperienceYears() != null) {
                    user.setExperienceYears(editedProfile.getExperienceYears());
                }
                if (editedProfile.getSkkLevel() != null) {
                    user.setSkkLevel(editedProfile.getSkkLevel());
                }
                if (editedProfile.getCurrentLocation() != null) {
                    user.setCurrentLocation(editedProfile.getCurrentLocation());
                }
                if (editedProfile.getPreferredLocations() != null) {
                    user.setPreferredLocations(editedProfile.getPreferredLocations());
                }
                if (editedProfile.getSkill() != null) {
                    user.setSkill(editedProfile.getSkill());
                }

                userRepository.save(user);
                return convertToDTO(user);
            } catch (IllegalArgumentException e) {
                throw e;
            }
        }

        return null;
    }

    @Override
    public User findByEmail(String email) {
        Optional<User> userProfileOptional = userRepository.findByEmail(email);
        return userProfileOptional.orElse(null);
    }

    private UserResponseDTO convertToDTO(User user) {
        UserResponseDTO dto = new UserResponseDTO();
        dto.setId(user.getId());
        dto.setFirstName(user.getFirstName());
        dto.setLastName(user.getLastName());
        dto.setEmail(user.getEmail());
        dto.setPhoneNumber(user.getPhoneNumber());
        dto.setPhoto(user.getPhoto());
        dto.setAboutMe(user.getAboutMe());
        dto.setNik(user.getNik());
        dto.setNpwp(user.getNpwp());
        dto.setPhotoKtp(user.getPhotoKtp());
        dto.setPhotoNpwp(user.getPhotoNpwp());
        dto.setPhotoIjazah(user.getPhotoIjazah());
        dto.setExperienceYears(user.getExperienceYears());
        dto.setSkkLevel(user.getSkkLevel());
        dto.setCurrentLocation(user.getCurrentLocation());
        dto.setPreferredLocations(user.getPreferredLocations());
        dto.setSkill(user.getSkill());
        return dto;
    }


}
