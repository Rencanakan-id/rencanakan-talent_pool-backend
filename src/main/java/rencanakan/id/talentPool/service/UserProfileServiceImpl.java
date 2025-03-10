package rencanakan.id.talentPool.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import rencanakan.id.talentPool.dto.UserProfileResponseDTO;
import rencanakan.id.talentPool.model.UserProfile;
import rencanakan.id.talentPool.repository.UserProfileRepository;

import java.util.Optional;

@Service
public class UserProfileServiceImpl implements UserProfileService {

    @Autowired
    private UserProfileRepository userProfileRepository;

    @Override
    public UserProfileResponseDTO getById(String id) {
        Optional<UserProfile> userProfileOptional = userProfileRepository.findById(id);
        
        if (userProfileOptional.isPresent()) {
            UserProfile userProfile = userProfileOptional.get();
            return convertToDTO(userProfile);
        }
        
        return null;
    }

    @Override
    public UserProfileResponseDTO editProfile(String id, UserProfile editedProfile) {
        Optional<UserProfile> userProfileOptional = userProfileRepository.findById(id);

        if (userProfileOptional.isPresent()) {
            UserProfile userProfile = userProfileOptional.get();

            try {
                if (editedProfile.getEmail() != null) {
                    validateEmail(editedProfile.getEmail());
                    userProfile.setEmail(editedProfile.getEmail());
                }
                if (editedProfile.getPassword() != null) {
                    validatePassword(editedProfile.getPassword());
                    userProfile.setPassword(editedProfile.getPassword());
                }
                if (editedProfile.getNik() != null) {
                    validateNik(editedProfile.getNik());
                    userProfile.setNik(editedProfile.getNik());
                }

                if (editedProfile.getFirstName() != null) {
                    userProfile.setFirstName(editedProfile.getFirstName());
                }
                if (editedProfile.getLastName() != null) {
                    userProfile.setLastName(editedProfile.getLastName());
                }
                if (editedProfile.getEmail() != null) {
                    userProfile.setEmail(editedProfile.getEmail());
                }
                if (editedProfile.getPassword() != null) {
                    userProfile.setPassword(editedProfile.getPassword());
                }
                if (editedProfile.getPhoneNumber() != null) {
                    userProfile.setPhoneNumber(editedProfile.getPhoneNumber());
                }
                if (editedProfile.getAddress() != null) {
                    userProfile.setAddress(editedProfile.getAddress());
                }
                if (editedProfile.getJob() != null) {
                    userProfile.setJob(editedProfile.getJob());
                }
                if (editedProfile.getPhoto() != null) {
                    userProfile.setPhoto(editedProfile.getPhoto());
                }
                if (editedProfile.getAboutMe() != null) {
                    userProfile.setAboutMe(editedProfile.getAboutMe());
                }
                if (editedProfile.getNik() != null) {
                    userProfile.setNik(editedProfile.getNik());
                }
                if (editedProfile.getNpwp() != null) {
                    userProfile.setNpwp(editedProfile.getNpwp());
                }
                if (editedProfile.getPhotoKtp() != null) {
                    userProfile.setPhotoKtp(editedProfile.getPhotoKtp());
                }
                if (editedProfile.getPhotoNpwp() != null) {
                    userProfile.setPhotoNpwp(editedProfile.getPhotoNpwp());
                }
                if (editedProfile.getPhotoIjazah() != null) {
                    userProfile.setPhotoIjazah(editedProfile.getPhotoIjazah());
                }
                if (editedProfile.getExperienceYears() != null) {
                    userProfile.setExperienceYears(editedProfile.getExperienceYears());
                }
                if (editedProfile.getSkkLevel() != null) {
                    userProfile.setSkkLevel(editedProfile.getSkkLevel());
                }
                if (editedProfile.getCurrentLocation() != null) {
                    userProfile.setCurrentLocation(editedProfile.getCurrentLocation());
                }
                if (editedProfile.getPreferredLocations() != null) {
                    userProfile.setPreferredLocations(editedProfile.getPreferredLocations());
                }
                if (editedProfile.getSkill() != null) {
                    userProfile.setSkill(editedProfile.getSkill());
                }

                userProfileRepository.save(userProfile);
                return convertToDTO(userProfile);
            } catch (IllegalArgumentException e) {
                throw e;
            }
        }

        return null;
    }

    private void validateEmail(String email) {
        if (!email.matches("^[A-Za-z0-9+_.-]+@(.+)$")) {
            throw new IllegalArgumentException("Invalid email format");
        }
    }

    private void validatePassword(String password) {
        if (password.length() < 8) {
            throw new IllegalArgumentException("Password must be at least 8 characters");
        }
    }

    private void validateNik(String nik) {
        if (nik.length() != 16) {
            throw new IllegalArgumentException("NIK must be exactly 16 digits");
        }
    }

    private UserProfileResponseDTO convertToDTO(UserProfile userProfile) {
        UserProfileResponseDTO dto = new UserProfileResponseDTO();
        dto.setId(userProfile.getId());
        dto.setFirstName(userProfile.getFirstName());
        dto.setLastName(userProfile.getLastName());
        dto.setEmail(userProfile.getEmail());
        dto.setPhoneNumber(userProfile.getPhoneNumber());
        dto.setAddress(userProfile.getAddress());
        dto.setJob(userProfile.getJob());
        dto.setPhoto(userProfile.getPhoto());
        dto.setAboutMe(userProfile.getAboutMe());
        dto.setNik(userProfile.getNik());
        dto.setNpwp(userProfile.getNpwp());
        dto.setPhotoKtp(userProfile.getPhotoKtp());
        dto.setPhotoNpwp(userProfile.getPhotoNpwp());
        dto.setPhotoIjazah(userProfile.getPhotoIjazah());
        dto.setExperienceYears(userProfile.getExperienceYears());
        dto.setSkkLevel(userProfile.getSkkLevel());
        dto.setCurrentLocation(userProfile.getCurrentLocation());
        dto.setPreferredLocations(userProfile.getPreferredLocations());
        dto.setSkill(userProfile.getSkill());
        return dto;
    }
}
