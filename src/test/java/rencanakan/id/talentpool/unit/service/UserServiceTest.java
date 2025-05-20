package rencanakan.id.talentpool.unit.service;

import jakarta.persistence.EntityNotFoundException;

import jakarta.validation.Validator;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import java.util.*;
import java.lang.reflect.Field;
import java.util.Comparator;

import rencanakan.id.talentpool.dto.FilterTalentDTO;
import rencanakan.id.talentpool.dto.UserRequestDTO;
import rencanakan.id.talentpool.dto.UserResponseDTO;
import rencanakan.id.talentpool.dto.UserResponseWithPagingDTO;
import rencanakan.id.talentpool.mapper.DTOMapper;
import rencanakan.id.talentpool.model.User;
import rencanakan.id.talentpool.repository.UserRepository;
import rencanakan.id.talentpool.service.UserServiceImpl;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private Validator mockValidator;

    @InjectMocks
    private UserServiceImpl userService;

    private String testUserId = "user123";
    private User testUser;
    Pageable page;


    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setId(testUserId);
        testUser.setFirstName("John");
        testUser.setLastName("Doe");
        testUser.setEmail("john.doe@example.com");
        testUser.setPassword("password123");
        testUser.setNik("1234567891011121");

        page = PageRequest.of(0, 1);

    }

    private User mockUser(String firstName) {
        User user = new User();
        user.setFirstName(firstName);
        return user;
    }




    @Nested
    class ReadUserTests {
        @Test
        void getById_WithValidId_ReturnsUser() {
            when(userRepository.findById(testUserId)).thenReturn(Optional.of(testUser));

            UserResponseDTO result = userService.getById(testUserId);

            assertNotNull(result);
            assertEquals(testUserId, result.getId());
            assertEquals("John", result.getFirstName());
            assertEquals("Doe", result.getLastName());
            verify(userRepository, times(1)).findById(testUserId);
        }

        @Test
        void getById_WithNonExistentId_ThrowsEntityNotFoundException() {
            String nonExistentId = "nonexistent";
            when(userRepository.findById(nonExistentId)).thenReturn(Optional.empty());

            Exception exception = assertThrows(EntityNotFoundException.class, () -> {
                userService.getById(nonExistentId);
            });
            assertEquals("User with ID " + nonExistentId + " not found", exception.getMessage());
            verify(userRepository, times(1)).findById(nonExistentId);
        }

        @Test
        void getById_WithEmptyId_ThrowsEntityNotFoundException() {
            String emptyId = "";
            when(userRepository.findById(emptyId)).thenReturn(Optional.empty());

            Exception exception = assertThrows(EntityNotFoundException.class, () -> {
                userService.getById(emptyId);
            });
            assertEquals("User with ID " + emptyId + " not found", exception.getMessage());
            verify(userRepository, times(1)).findById(emptyId);
        }

        @Test
        void findByEmail_WithValidEmail_ReturnsUser() {
            String testEmail = "john.doe@example.com";
            when(userRepository.findByEmail(testEmail)).thenReturn(Optional.of(testUser));

            User result = userService.findByEmail(testEmail);

            assertNotNull(result);
            assertEquals(testUserId, result.getId());
            assertEquals("John", result.getFirstName());
            assertEquals("Doe", result.getLastName());
            assertEquals(testEmail, result.getEmail());
            verify(userRepository, times(1)).findByEmail(testEmail);
        }

        @Test
        void findByEmail_WithNonExistentEmail_ReturnsNull() {
            String nonExistentEmail = "nonexistent@example.com";
            when(userRepository.findByEmail(nonExistentEmail)).thenReturn(Optional.empty());

            Exception exception = assertThrows(EntityNotFoundException.class, () -> {
                userService.findByEmail(nonExistentEmail);
            });

            assertEquals("User not found with email: " + nonExistentEmail, exception.getMessage());
            verify(userRepository, times(1)).findByEmail(nonExistentEmail);
        }

        @Test
        void findByEmail_WithEmptyEmail_ReturnsNull() {
            String emptyEmail = "";
            when(userRepository.findByEmail(emptyEmail)).thenReturn(Optional.empty());

            Exception exception = assertThrows(EntityNotFoundException.class, () -> {
                userService.findByEmail(emptyEmail);
            });

            assertEquals("User not found with email: " + emptyEmail, exception.getMessage());
            verify(userRepository, times(1)).findByEmail(emptyEmail);
        }
    }

    @Nested
    class UpdateUserTests {
        private List<String> preferredLocations;
        private UserRequestDTO updatedUserData;

        @BeforeEach
        void setUp() {
            preferredLocations = new ArrayList<>();
            preferredLocations.add("Bekasi");
            preferredLocations.add("Depok");
            preferredLocations.add("Bogor");

            updatedUserData = new UserRequestDTO();
            updatedUserData.setFirstName("Jane");
            updatedUserData.setLastName("Doe");
            updatedUserData.setEmail("jane.doe@example.com");
            updatedUserData.setNik("1234567891011121");
        }

        @Test
        void editById_WithValidData_UpdatesSuccessfully() {
            when(userRepository.findById(testUserId)).thenReturn(Optional.of(testUser));

            UserResponseDTO editResult = userService.editById(testUserId, updatedUserData);

            assertNotNull(editResult);
            assertEquals(testUserId, editResult.getId());
            assertEquals("Jane", editResult.getFirstName());
            assertEquals("Doe", editResult.getLastName());
            assertEquals("jane.doe@example.com", editResult.getEmail());

            verify(userRepository, times(1)).findById(testUserId);
            verify(userRepository, times(1)).save(any(User.class));
        }

        @Test
        void editById_WithNoChanges_RetainsOriginalValues() {
            when(userRepository.findById(testUserId)).thenReturn(Optional.of(testUser));
            UserRequestDTO emptyUpdateData = new UserRequestDTO();

            UserResponseDTO editResult = userService.editById(testUserId, emptyUpdateData);

            assertEquals(testUserId, editResult.getId());
            assertEquals("John", editResult.getFirstName());
            assertEquals("Doe", editResult.getLastName());
            assertEquals("john.doe@example.com", editResult.getEmail());
            verify(userRepository, times(1)).save(any(User.class));
        }

        @Test
        void editById_WithInvalidEmail_FailsValidation() {
            when(userRepository.findById(testUserId)).thenReturn(Optional.of(testUser));

            UserRequestDTO invalidUserRequest = new UserRequestDTO();
            invalidUserRequest.setEmail("invalid-email");

            when(mockValidator.validate(any(User.class)))
                .thenAnswer(invocation -> {
                    Set<jakarta.validation.ConstraintViolation<User>> violations = new HashSet<>();
                    @SuppressWarnings("unchecked")
                    jakarta.validation.ConstraintViolation<User> violation = mock(jakarta.validation.ConstraintViolation.class);
                    when(violation.getPropertyPath()).thenReturn(mock(jakarta.validation.Path.class));
                    when(violation.getPropertyPath().toString()).thenReturn("email");
                    when(violation.getMessage()).thenReturn("Invalid email format");
                    violations.add(violation);
                    return violations;
                });

            Exception exception = assertThrows(IllegalArgumentException.class, () -> {
                userService.editById(testUserId, invalidUserRequest);
            });
            assertTrue(exception.getMessage().startsWith("Failed to update user"));
        }

        @Test
        void editById_WithInvalidNIK_FailsValidation() {
            when(userRepository.findById(testUserId)).thenReturn(Optional.of(testUser));

            UserRequestDTO invalidUserRequest = new UserRequestDTO();
            invalidUserRequest.setNik("12345");

            when(mockValidator.validate(any(User.class)))
                .thenAnswer(invocation -> {
                    Set<jakarta.validation.ConstraintViolation<User>> violations = new HashSet<>();
                    @SuppressWarnings("unchecked")
                    jakarta.validation.ConstraintViolation<User> violation = mock(jakarta.validation.ConstraintViolation.class);
                    when(violation.getPropertyPath()).thenReturn(mock(jakarta.validation.Path.class));
                    when(violation.getPropertyPath().toString()).thenReturn("nik");
                    when(violation.getMessage()).thenReturn("NIK must be exactly 16 digits");
                    violations.add(violation);
                    return violations;
                });

            Exception exception = assertThrows(IllegalArgumentException.class, () -> {
                userService.editById(testUserId, invalidUserRequest);
            });
            assertTrue(exception.getMessage().startsWith("Failed to update user"));
        }

        @Test
        void editById_WithNonExistentId_ThrowsEntityNotFoundException() {
            when(userRepository.findById("invalid-id")).thenReturn(Optional.empty());
            User user = new User();

            UserRequestDTO invalidUserRequest = DTOMapper.map(user, UserRequestDTO.class);

            Exception exception = assertThrows(RuntimeException.class, () ->
                    userService.editById("invalid-id", invalidUserRequest)
            );
            assertEquals("User with ID invalid-id not found", exception.getMessage());
        }

        @Test
        void editById_WithNameTooLong_FailsValidation() {
            when(userRepository.findById(testUserId)).thenReturn(Optional.of(testUser));

            UserRequestDTO invalidUserRequest = new UserRequestDTO();
            invalidUserRequest.setFirstName("A".repeat(100));

            when(mockValidator.validate(any(User.class)))
                .thenAnswer(invocation -> {
                    Set<jakarta.validation.ConstraintViolation<User>> violations = new HashSet<>();
                    @SuppressWarnings("unchecked")
                    jakarta.validation.ConstraintViolation<User> violation = mock(jakarta.validation.ConstraintViolation.class);
                    when(violation.getPropertyPath()).thenReturn(mock(jakarta.validation.Path.class));
                    when(violation.getPropertyPath().toString()).thenReturn("firstName");
                    when(violation.getMessage()).thenReturn("First name exceeds maximum length");
                    violations.add(violation);
                    return violations;
                });

            Exception exception = assertThrows(IllegalArgumentException.class, () -> {
                userService.editById(testUserId, invalidUserRequest);
            });
            assertTrue(exception.getMessage().startsWith("Failed to update user"));
        }
    }

    @Nested
    class UserDetailsServiceTests {

        @Test
        void loadUserByUsername_WithValidEmail_ReturnsUser() {
            String testEmail = "john.doe@example.com";
            when(userRepository.findByEmail(testEmail)).thenReturn(Optional.of(testUser));

            User result = userService.loadUserByUsername(testEmail);

            assertNotNull(result);
            assertEquals(testUserId, result.getId());
            assertEquals("John", result.getFirstName());
            assertEquals("Doe", result.getLastName());
            assertEquals(testEmail, result.getEmail());
            verify(userRepository, times(1)).findByEmail(testEmail);
        }

        @Test
        void loadUserByUsername_WithNonExistentEmail_ThrowsUsernameNotFoundException() {
            String nonExistentEmail = "nonexistent@example.com";
            when(userRepository.findByEmail(nonExistentEmail)).thenReturn(Optional.empty());

            Exception exception = assertThrows(UsernameNotFoundException.class, () -> {
                userService.loadUserByUsername(nonExistentEmail);
            });

            assertEquals("User not found", exception.getMessage());
            verify(userRepository, times(1)).findByEmail(nonExistentEmail);
        }
    }
    @Nested
    class FilterTalentTest{
        @Test
        void filter_ShouldReturnUsersOrderedAlphabeticallyByName() {
            FilterTalentDTO filter = new FilterTalentDTO();
            Pageable pageable = PageRequest.of(0, 10);

            // Create users with different combinations of first and last names
            User user1 = new User();
            user1.setFirstName("Alice");
            user1.setLastName("Smith");

            User user2 = new User();
            user2.setFirstName("Bob");
            user2.setLastName("Jones");

            User user3 = new User();
            user3.setFirstName("Alice");
            user3.setLastName("Johnson");

            List<User> users = Arrays.asList(user2, user3, user1); // Unordered list

            // Mock that the repository will return the users in alphabetical order
            // This simulates what Spring Data JPA would do with Sort.by("firstName").and(Sort.by("lastName"))
            when(userRepository.findAll(any(Specification.class), any(Pageable.class)))
                    .thenAnswer(invocation -> {
                        Pageable pageableArg = invocation.getArgument(1);
                        if (pageableArg.getSort().isSorted()) {
                            // Sort the list by firstName and then by lastName
                            List<User> sortedUsers = new ArrayList<>(users);
                            sortedUsers.sort(Comparator.comparing(User::getFirstName)
                                             .thenComparing(User::getLastName));
                            return new PageImpl<>(sortedUsers, pageableArg, sortedUsers.size());
                        }
                        return new PageImpl<>(users, pageableArg, users.size());
                    });

            // Mock the DTOMapper to return DTOs with the same names
            try (MockedStatic<DTOMapper> mockedMapper = Mockito.mockStatic(DTOMapper.class)) {
                mockedMapper.when(() -> DTOMapper.map(eq(user1), eq(UserResponseDTO.class)))
                        .thenReturn(UserResponseDTO.builder()
                                   .firstName("Alice")
                                   .lastName("Smith")
                                   .build());
                mockedMapper.when(() -> DTOMapper.map(eq(user2), eq(UserResponseDTO.class)))
                        .thenReturn(UserResponseDTO.builder()
                                   .firstName("Bob")
                                   .lastName("Jones")
                                   .build());
                mockedMapper.when(() -> DTOMapper.map(eq(user3), eq(UserResponseDTO.class)))
                        .thenReturn(UserResponseDTO.builder()
                                   .firstName("Alice")
                                   .lastName("Johnson")
                                   .build());

                UserResponseWithPagingDTO result = userService.filter(filter, pageable);

                // Check that users are sorted first by firstName, then by lastName
                List<UserResponseDTO> sortedUsers = result.getUsers();
                assertEquals(3, sortedUsers.size());
                assertEquals("Alice", sortedUsers.get(0).getFirstName());
                assertEquals("Johnson", sortedUsers.get(0).getLastName());
                assertEquals("Alice", sortedUsers.get(1).getFirstName());
                assertEquals("Smith", sortedUsers.get(1).getLastName());
                assertEquals("Bob", sortedUsers.get(2).getFirstName());
            }
        }
        @Test
        void filter_withValidData_returnsMatchingUsers() {
            FilterTalentDTO filter = FilterTalentDTO.builder().name("john").preferredLocations( Arrays.asList("Jakarta")).priceRange(Arrays.asList(0.00,200000.00)).skills(Arrays.asList("Java")).build();

            User user = new User();
            user.setFirstName("John");
            user.setLastName("Doe");

            when(userRepository.findAll(any(Specification.class), any(Pageable.class)))
                    .thenReturn(new PageImpl<>(List.of(user)));


            try (MockedStatic<DTOMapper> mockedMapper = Mockito.mockStatic(DTOMapper.class)) {
                UserResponseDTO dto = new UserResponseDTO();
                dto.setFirstName("John");
                dto.setLastName("Doe");

                mockedMapper.when(() -> DTOMapper.map(user, UserResponseDTO.class)).thenReturn(dto);


                UserResponseWithPagingDTO result = userService.filter(filter, page);

                Assertions.assertEquals(1, result.getUsers().size());
                Assertions.assertEquals("John", result.getUsers().get(0).getFirstName());
            }
        }

        @Test
        void filter_withOnlyName_shouldReturnMatchingUsers() {
            FilterTalentDTO filter = FilterTalentDTO.builder().name("doe").build();

            User user = mockUser("Doe");

            when(userRepository.findAll(any(Specification.class), any(Pageable.class)))
                    .thenReturn(new PageImpl<>(List.of(user)));


            try (MockedStatic<DTOMapper> mockedMapper = Mockito.mockStatic(DTOMapper.class)) {
                mockedMapper.when(() -> DTOMapper.map(user, UserResponseDTO.class)).thenReturn(UserResponseDTO.builder().firstName("Doe").build());

                UserResponseWithPagingDTO result = userService.filter(filter, page);

                Assertions.assertEquals(1, result.getUsers().size());
            }
        }

        @Test
        void filter_withOnlyPreferredLocations_shouldReturnMatchingUsers() {
            FilterTalentDTO filter = FilterTalentDTO.builder()
                    .preferredLocations(List.of("Bandung"))
                    .build();

            User user = mockUser("A");

            when(userRepository.findAll(any(Specification.class), any(Pageable.class)))
                    .thenReturn(new PageImpl<>(List.of(user)));

            try (MockedStatic<DTOMapper> mockedMapper = Mockito.mockStatic(DTOMapper.class)) {
                mockedMapper.when(() -> DTOMapper.map(user, UserResponseDTO.class)).thenReturn(UserResponseDTO.builder().preferredLocations(Arrays.asList("Bandung")).build());

                UserResponseWithPagingDTO result = userService.filter(filter, page);


                Assertions.assertEquals(1, result.getUsers().size());
            }
        }
        @Test
        void filter_withOnlySkills_shouldReturnMatchingUsers() {
            FilterTalentDTO filter = FilterTalentDTO.builder()
                    .skills(List.of("React"))
                    .build();

            User user = mockUser("B");

            when(userRepository.findAll(any(Specification.class), any(Pageable.class)))
                    .thenReturn(new PageImpl<>(List.of(user)));

            try (MockedStatic<DTOMapper> mockedMapper = Mockito.mockStatic(DTOMapper.class)) {
                mockedMapper.when(() -> DTOMapper.map(user, UserResponseDTO.class)).thenReturn(UserResponseDTO.builder().skill("React").build());

                UserResponseWithPagingDTO result = userService.filter(filter, page);


                Assertions.assertEquals(1, result.getUsers().size());
            }
        }

        @Test
        void filter_withOnlyPriceRange_shouldReturnMatchingUsers() {
            FilterTalentDTO filter = FilterTalentDTO.builder()
                    .priceRange(List.of(100.0, 200.0))
                    .build();

            User user = mockUser("C");

            when(userRepository.findAll(any(Specification.class), any(Pageable.class)))
                    .thenReturn(new PageImpl<>(List.of(user)));

            try (MockedStatic<DTOMapper> mockedMapper = Mockito.mockStatic(DTOMapper.class)) {
                mockedMapper.when(() -> DTOMapper.map(user, UserResponseDTO.class)).thenReturn(UserResponseDTO.builder().price(150).build());

                UserResponseWithPagingDTO result = userService.filter(filter, page);


                Assertions.assertEquals(1, result.getUsers().size());
            }
        }

        @Test
        void filter_withInvalidPriceRange_shouldIgnoreIt() {
            FilterTalentDTO filter = FilterTalentDTO.builder()
                    .priceRange(List.of(100.0)) // kurang dari dua
                    .build();

            User user = mockUser("D");

            when(userRepository.findAll(any(Specification.class), any(Pageable.class)))
                    .thenReturn(new PageImpl<>(List.of(user)));

            try (MockedStatic<DTOMapper> mockedMapper = Mockito.mockStatic(DTOMapper.class)) {
                mockedMapper.when(() -> DTOMapper.map(user, UserResponseDTO.class)).thenReturn(UserResponseDTO.builder().firstName("D").build());

                UserResponseWithPagingDTO result = userService.filter(filter, page);


                Assertions.assertEquals(1, result.getUsers().size());
            }
        }


        @Test
        void filter_withNonMatchingName_returnsEmptyList() {
            FilterTalentDTO filter = FilterTalentDTO.builder().name("nonexistent").build();

            when(userRepository.findAll(any(Specification.class), any(Pageable.class)))
                    .thenReturn(new PageImpl<>(Collections.emptyList()));


            EntityNotFoundException thrown = Assertions.assertThrows(
                    EntityNotFoundException.class,
                    () -> userService.filter(filter, page),
                    "Expected filter() to throw, but it didn't"
            );

            Assertions.assertEquals("No users found", thrown.getMessage());
        }

        @Test
        void filter_withNullName_returnsAllUsers() {
            FilterTalentDTO filter = FilterTalentDTO.builder().name(null).build();

            User user1 = new User();
            user1.setFirstName("Alice");
            user1.setLastName("Smith");

            User user2 = new User();
            user2.setFirstName("Bob");
            user2.setLastName("Johnson");

            List<User> allUsers = List.of(user1, user2);

            when(userRepository.findAll(any(Specification.class), any(Pageable.class)))
                    .thenReturn(new PageImpl<>(allUsers));

            try (MockedStatic<DTOMapper> mockedMapper = Mockito.mockStatic(DTOMapper.class)) {
                mockedMapper.when(() -> DTOMapper.map(eq(user1), eq(UserResponseDTO.class)))
                        .thenReturn(UserResponseDTO.builder().firstName("Alice").lastName("Smith").build());

                mockedMapper.when(() -> DTOMapper.map(eq(user2), eq(UserResponseDTO.class)))
                        .thenReturn(UserResponseDTO.builder().firstName("Bob").lastName("Johnson").build());

                UserResponseWithPagingDTO result = userService.filter(filter, page);


                Assertions.assertEquals(2, result.getUsers().size());
                Assertions.assertEquals("Alice", result.getUsers().get(0).getFirstName());
                Assertions.assertEquals("Bob", result.getUsers().get(1).getFirstName());
            }
        }

        @Test
        void filter_withEmptyName_shouldTreatAsNoNameFilter() {

            FilterTalentDTO filter = FilterTalentDTO.builder().name("  ").build();

            User user1 = new User();
            user1.setFirstName("Alice");

            List<User> allUsers = List.of(user1);

            when(userRepository.findAll(any(Specification.class), any(Pageable.class)))
                    .thenReturn(new PageImpl<>(allUsers));

            try (MockedStatic<DTOMapper> mockedMapper = Mockito.mockStatic(DTOMapper.class)) {
                mockedMapper.when(() -> DTOMapper.map(eq(user1), eq(UserResponseDTO.class)))
                        .thenReturn(UserResponseDTO.builder().firstName("Alice").build());

                UserResponseWithPagingDTO result = userService.filter(filter, page);

                Assertions.assertEquals(1, result.getUsers().size());
                Assertions.assertEquals("Alice", result.getUsers().get(0).getFirstName());
            }
        }

        @Test
        void filter_withNameConditionBranches() {

            FilterTalentDTO filterWithNullName = FilterTalentDTO.builder().name(null).build();


            FilterTalentDTO filterWithWhitespaceName = FilterTalentDTO.builder().name("  ").build();


            FilterTalentDTO filterWithEmptyName = FilterTalentDTO.builder().name("").build();


            FilterTalentDTO filterWithValidName = FilterTalentDTO.builder().name("John").build();

            User testUserLocal = new User();
            testUserLocal.setFirstName("Test");

            when(userRepository.findAll(any(Specification.class), any(Pageable.class)))
                    .thenReturn(new PageImpl<>(List.of(testUserLocal)));


            try (MockedStatic<DTOMapper> mockedMapper = Mockito.mockStatic(DTOMapper.class)) {
                mockedMapper.when(() -> DTOMapper.map(eq(testUserLocal), eq(UserResponseDTO.class)))
                        .thenReturn(UserResponseDTO.builder().firstName("Test").build());


                UserResponseWithPagingDTO resultNull = userService.filter(filterWithNullName, page);
                UserResponseWithPagingDTO resultWhitespace = userService.filter(filterWithWhitespaceName, page);
                UserResponseWithPagingDTO resultEmpty = userService.filter(filterWithEmptyName, page);
                UserResponseWithPagingDTO resultValid = userService.filter(filterWithValidName, page);

                assertEquals(1, resultNull.getUsers().size());
                assertEquals(1, resultWhitespace.getUsers().size());
                assertEquals(1, resultEmpty.getUsers().size());
                assertEquals(1, resultValid.getUsers().size());

                verify(userRepository, times(4)).findAll(any(Specification.class), any(Pageable.class));
            }
        }

        @Test
        void filter_withEmptyStringName_shouldTreatAsNoNameFilter() {

            FilterTalentDTO filter = FilterTalentDTO.builder().name("").build();

            User user1 = new User();
            user1.setFirstName("Alice");

            List<User> allUsers = List.of(user1);

            when(userRepository.findAll(any(Specification.class), any(Pageable.class)))
                    .thenReturn(new PageImpl<>(allUsers));

            try (MockedStatic<DTOMapper> mockedMapper = Mockito.mockStatic(DTOMapper.class)) {
                mockedMapper.when(() -> DTOMapper.map(eq(user1), eq(UserResponseDTO.class)))
                        .thenReturn(UserResponseDTO.builder().firstName("Alice").build());

                UserResponseWithPagingDTO result = userService.filter(filter, page);


                Assertions.assertEquals(1, result.getUsers().size());
                Assertions.assertEquals("Alice", result.getUsers().get(0).getFirstName());
            }
        }

        @Test
        void filter_withEmptyStringName_shouldHandleAsNoNameFilter() {

            FilterTalentDTO filter = new FilterTalentDTO();
            filter.setName(""); // Empty string

            when(userRepository.findAll(any(Specification.class), any(Pageable.class)))
                .thenReturn(new PageImpl<>(List.of(mockUser("John"))));

            UserResponseWithPagingDTO result = userService.filter(filter, page);


            assertNotNull(result);
            assertFalse(result.getUsers().isEmpty());
            assertEquals("John", result.getUsers().get(0).getFirstName());
        }

    }
}
