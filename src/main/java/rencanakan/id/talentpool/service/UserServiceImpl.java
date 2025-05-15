package rencanakan.id.talentpool.service;

import jakarta.persistence.EntityNotFoundException;
import jakarta.persistence.criteria.Join;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import jakarta.persistence.criteria.Predicate;
import rencanakan.id.talentpool.dto.FilterTalentDTO;
import rencanakan.id.talentpool.dto.UserRequestDTO;
import rencanakan.id.talentpool.dto.UserResponseDTO;
import rencanakan.id.talentpool.dto.UserResponseWithPagingDTO;
import rencanakan.id.talentpool.mapper.DTOMapper;
import rencanakan.id.talentpool.model.User;
import rencanakan.id.talentpool.repository.UserRepository;
import rencanakan.id.talentpool.specification.NameSpecification;
import rencanakan.id.talentpool.specification.LocationSpecification;
import rencanakan.id.talentpool.specification.SkillsSpecification;
import rencanakan.id.talentpool.specification.PriceSpecification;


import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;


@Service
public class UserServiceImpl implements UserService, UserDetailsService {
    private final UserRepository userRepository;

    public UserServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserResponseDTO getById(String id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("User with ID " + id + " not found"));
        
        return DTOMapper.map(user, UserResponseDTO.class);
    }

    @Override
    public UserResponseDTO editById(String id, UserRequestDTO edited) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("User with ID " + id + " not found"));

        try {
            updateUserFields(user, edited);
            userRepository.save(user);
            return DTOMapper.map(user, UserResponseDTO.class);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Failed to update user: " + e.getMessage(), e);
        }
    }

    private void updateUserFields(User user, UserRequestDTO edited) {
        updateIfNotNull(edited.getEmail(), user::setEmail);
        updateIfNotNull(edited.getFirstName(), user::setFirstName);
        updateIfNotNull(edited.getLastName(), user::setLastName);
        updateIfNotNull(edited.getPhoneNumber(), user::setPhoneNumber);
        updateIfNotNull(edited.getPrice(), user::setPrice);

        updateIfNotNull(edited.getPhoto(), user::setPhoto);
        updateIfNotNull(edited.getAboutMe(), user::setAboutMe);
        updateIfNotNull(edited.getNik(), user::setNik);
        updateIfNotNull(edited.getNpwp(), user::setNpwp);

        updateIfNotNull(edited.getPhotoKtp(), user::setPhotoKtp);
        updateIfNotNull(edited.getPhotoNpwp(), user::setPhotoNpwp);
        updateIfNotNull(edited.getPhotoIjazah(), user::setPhotoIjazah);
        
        updateIfNotNull(edited.getExperienceYears(), user::setExperienceYears);
        updateIfNotNull(edited.getSkkLevel(), user::setSkkLevel);
        updateIfNotNull(edited.getCurrentLocation(), user::setCurrentLocation);
        updateIfNotNull(edited.getPreferredLocations(), user::setPreferredLocations);
        updateIfNotNull(edited.getSkill(), user::setSkill);
    }

    private <T> void updateIfNotNull(T value, java.util.function.Consumer<T> setter) {
        if (value != null) {
            setter.accept(value);
        }
    }

    @Override
    public User findByEmail(String email) {
        Optional<User> userOptional = userRepository.findByEmail(email);
        return userOptional.orElseThrow(() -> new EntityNotFoundException("User not found with email: " + email));
    }

    @Override
    public UserResponseWithPagingDTO filter(FilterTalentDTO filter, Pageable page) {
        Specification<User> specification = Specification.<User>where(null)
            .and(NameSpecification.hasName(filter.getName()))
            .and(LocationSpecification.hasPreferredLocations(filter.getPreferredLocations()))
            .and(SkillsSpecification.hasSkills(filter.getSkills()))
            .and(PriceSpecification.inPriceRange(filter.getPriceRange()));

        Page<User> userPage = userRepository.findAll(specification, page);
        if(userPage.isEmpty()){
            throw  new EntityNotFoundException("No users found");
        }

        List<UserResponseDTO> userDTOs = userPage.getContent().stream()
                .map(user -> DTOMapper.map(user, UserResponseDTO.class))
                .collect(Collectors.toList());

        return UserResponseWithPagingDTO.builder()
            .users(userDTOs)
            .page(userPage.getNumber())
            .size(userPage.getSize())
            .totalPages(userPage.getTotalPages())
            .build();
    }



    @Override
    public User loadUserByUsername(String username) throws UsernameNotFoundException {
        return userRepository.findByEmail(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
    }


}
