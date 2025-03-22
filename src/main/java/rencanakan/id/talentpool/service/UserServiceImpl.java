package rencanakan.id.talentpool.service;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import rencanakan.id.talentpool.dto.UserRequestDTO;
import rencanakan.id.talentpool.dto.UserResponseDTO;
import rencanakan.id.talentpool.model.User;
import rencanakan.id.talentpool.repository.UserRepository;

import java.util.Optional;
import java.util.Set;

@Service
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final Validator validator;

    public UserServiceImpl(UserRepository userRepository, Validator validator) {
        this.userRepository = userRepository;
        this.validator = validator;
    }

    @Override
    public UserResponseDTO getById(String id) {
        Optional<User> userOptional = userRepository.findById(id);
        
        if (userOptional.isPresent()) {
            User user = userOptional.get();
            return convertToDTO(user);
        }
        
        return null;
    }

    @Override
    public UserResponseDTO editUser(String id, User edited) {
        Optional<User> userOptional = userRepository.findById(id);

        if (userOptional.isPresent()) {
            User user = userOptional.get();

            try {
                if (edited.getEmail() != null) {
                    user.setEmail(edited.getEmail());
                }
                if (edited.getPassword() != null) {
                    user.setPassword(edited.getPassword());
                }
                if (edited.getNik() != null) {
                    user.setNik(edited.getNik());
                }

                if (edited.getFirstName() != null) {
                    user.setFirstName(edited.getFirstName());
                }
                if (edited.getLastName() != null) {
                    user.setLastName(edited.getLastName());
                }
                if (edited.getEmail() != null) {
                    user.setEmail(edited.getEmail());
                }
                if (edited.getPassword() != null) {
                    user.setPassword(edited.getPassword());
                }
                if (edited.getPhoneNumber() != null) {
                    user.setPhoneNumber(edited.getPhoneNumber());
                }
                if (edited.getPhoto() != null) {
                    user.setPhoto(edited.getPhoto());
                }
                if (edited.getAboutMe() != null) {
                    user.setAboutMe(edited.getAboutMe());
                }
                if (edited.getNik() != null) {
                    user.setNik(edited.getNik());
                }
                if (edited.getNpwp() != null) {
                    user.setNpwp(edited.getNpwp());
                }
                if (edited.getPhotoKtp() != null) {
                    user.setPhotoKtp(edited.getPhotoKtp());
                }
                if (edited.getPhotoNpwp() != null) {
                    user.setPhotoNpwp(edited.getPhotoNpwp());
                }
                if (edited.getPhotoIjazah() != null) {
                    user.setPhotoIjazah(edited.getPhotoIjazah());
                }
                if (edited.getExperienceYears() != null) {
                    user.setExperienceYears(edited.getExperienceYears());
                }
                if (edited.getSkkLevel() != null) {
                    user.setSkkLevel(edited.getSkkLevel());
                }
                if (edited.getCurrentLocation() != null) {
                    user.setCurrentLocation(edited.getCurrentLocation());
                }
                if (edited.getPreferredLocations() != null) {
                    user.setPreferredLocations(edited.getPreferredLocations());
                }
                if (edited.getSkill() != null) {
                    user.setSkill(edited.getSkill());
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
        Optional<User> userOptional = userRepository.findByEmail(email);
        return userOptional.orElse(null);
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
