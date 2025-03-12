package rencanakan.id.talentPool.service;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import rencanakan.id.talentPool.dto.UserProfileRequestDTO;
import rencanakan.id.talentPool.dto.UserProfileResponseDTO;
import rencanakan.id.talentPool.model.User;
import rencanakan.id.talentPool.repository.UserProfileRepository;

import java.util.Optional;
import java.util.Set;

@Service
public class UserProfileServiceImpl implements UserProfileService {

    @Autowired
    private UserProfileRepository userProfileRepository;
    private final Validator validator;

    public UserProfileServiceImpl(UserProfileRepository userProfileRepository, Validator validator) {
        this.userProfileRepository = userProfileRepository;
        this.validator = validator;
    }

    @Override
    public UserProfileResponseDTO getById(String id) {
        Optional<User> userProfileOptional = userProfileRepository.findById(id);
        
        if (userProfileOptional.isPresent()) {
            User user = userProfileOptional.get();
            return convertToDTO(user);
        }
        
        return null;
    }

    @Override
    public UserProfileResponseDTO editProfile(String id, User editedProfile) {
        Optional<User> userProfileOptional = userProfileRepository.findById(id);

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
                if (editedProfile.getAddress() != null) {
                    user.setAddress(editedProfile.getAddress());
                }
                if (editedProfile.getJob() != null) {
                    user.setJob(editedProfile.getJob());
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

                userProfileRepository.save(user);
                return convertToDTO(user);
            } catch (IllegalArgumentException e) {
                throw e;
            }
        }

        return null;
    }

    private UserProfileResponseDTO convertToDTO(User user) {
        UserProfileResponseDTO dto = new UserProfileResponseDTO();
        dto.setId(user.getId());
        dto.setFirstName(user.getFirstName());
        dto.setLastName(user.getLastName());
        dto.setEmail(user.getEmail());
        dto.setPhoneNumber(user.getPhoneNumber());
        dto.setAddress(user.getAddress());
        dto.setJob(user.getJob());
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

    @Override
    public User createProfile(UserProfileRequestDTO request) {
        Set<ConstraintViolation<UserProfileRequestDTO>> violations = validator.validate(request);
        if (!violations.isEmpty()) {
            throw new IllegalArgumentException("Validation failed");
        }

        User newProfile = User.builder()
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .email(request.getEmail())
                .phoneNumber(request.getPhoneNumber())
                .address(request.getAddress())
                .job(request.getJob())
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
                .build();
        return userProfileRepository.save(newProfile);
    }
}
