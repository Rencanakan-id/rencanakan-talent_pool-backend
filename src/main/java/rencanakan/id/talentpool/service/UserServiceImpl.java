package rencanakan.id.talentpool.service;

import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;


import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
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

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class UserServiceImpl implements UserService, UserDetailsService {
    private static final String FIRST_NAME = "firstName";
    private static final String LAST_NAME = "lastName";
    private static final String CURRENT_LOCATION = "currentLocation";
    private static final String SKILL = "skill";
    private static final String PRICE = "price";
    private final UserRepository userRepository;
    private final Validator validator;

    public UserServiceImpl(UserRepository userRepository, Validator validator) {
        this.userRepository = userRepository;
        this.validator = validator;
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

            // Validate the user before saving
            Set<ConstraintViolation<User>> violations = validator.validate(user);
            if (!violations.isEmpty()) {
                StringBuilder sb = new StringBuilder();
                for (ConstraintViolation<User> violation : violations) {
                    sb.append(violation.getPropertyPath())
                      .append(": ")
                      .append(violation.getMessage())
                      .append("; ");
                }
                throw new IllegalArgumentException(sb.toString());
            }

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
        Specification<User> specification = (root, query, builder) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (Objects.nonNull(filter.getName()) && !filter.getName().trim().isEmpty()) {
                String keyword = "%" + filter.getName().toLowerCase() + "%";

                Predicate firstNamePredicate = builder.like(
                        builder.lower(root.get(FIRST_NAME)), keyword
                );

                Predicate lastNamePredicate = builder.like(
                        builder.lower(root.get(LAST_NAME)), keyword
                );

                Predicate fullNamePredicate = builder.like(
                        builder.lower(builder.concat(builder.concat(root.get(FIRST_NAME), " "), root.get(LAST_NAME))),
                        keyword
                );

                predicates.add(builder.or(firstNamePredicate, lastNamePredicate, fullNamePredicate));
            }

            if (Objects.nonNull(filter.getPreferredLocations()) && !filter.getPreferredLocations().isEmpty()) {
                List<Predicate> locationPredicates = filter.getPreferredLocations().stream()
                        .map(location -> builder.equal(
                                builder.lower(root.get(CURRENT_LOCATION)),
                                location.toLowerCase()
                        ))
                        .toList();

                predicates.add(builder.or(locationPredicates.toArray(new Predicate[0])));
            }

            if (Objects.nonNull(filter.getSkills()) && !filter.getSkills().isEmpty()) {
                List<Predicate> skillsPredicates = filter.getSkills().stream()
                        .map(skill -> builder.equal(
                                builder.lower(root.get(SKILL)),
                                skill.toLowerCase()
                        ))
                        .toList();

                predicates.add(builder.or(skillsPredicates.toArray(new Predicate[0])));
            }

            if (Objects.nonNull(filter.getPriceRange()) && filter.getPriceRange().size() == 2) {
                Double minPrice = filter.getPriceRange().get(0);
                Double maxPrice = filter.getPriceRange().get(1);

                Predicate pricePredicate = builder.between(root.get(PRICE), minPrice, maxPrice);

                predicates.add(pricePredicate);
            }

            return builder.and(predicates.toArray(new Predicate[0]));
        };

        // Create a Sort object that orders by firstName and then by lastName
        Sort sort = Sort.by(FIRST_NAME).and(Sort.by(LAST_NAME));
        Pageable pageable = PageRequest.of(page.getPageNumber(), page.getPageSize(), sort);
        Page<User> userPage = userRepository.findAll(specification, pageable);

        if(userPage.isEmpty()){
            throw  new EntityNotFoundException("No users found");
        }

        List<UserResponseDTO> userDTOs = userPage.getContent().stream()
                .map(user -> DTOMapper.map(user, UserResponseDTO.class))
                .toList();

        return UserResponseWithPagingDTO.builder().users(userDTOs).page( userPage.getNumber()).size(userPage.getSize()).totalPages(userPage.getTotalPages()).build();
    }

    @Override
    public User loadUserByUsername(String username) throws UsernameNotFoundException {
        return userRepository.findByEmail(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
    }
}
