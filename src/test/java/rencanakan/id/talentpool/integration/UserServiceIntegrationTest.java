package rencanakan.id.talentpool.integration;

import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.ActiveProfiles;
import rencanakan.id.talentpool.dto.FilterTalentDTO;
import rencanakan.id.talentpool.dto.UserResponseDTO;
import rencanakan.id.talentpool.dto.UserResponseWithPagingDTO;
import rencanakan.id.talentpool.model.User;
import rencanakan.id.talentpool.repository.UserRepository;
import rencanakan.id.talentpool.service.UserService;
import rencanakan.id.talentpool.service.UserServiceImpl;

import java.util.List;

import static org.junit.Assert.*;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@ActiveProfiles("test")
class UserServiceIntegrationTest {

    @Autowired
    private UserRepository userRepository;
    private UserService userService;
    private  Pageable page;

    @BeforeEach
    void setup() {
        userService = new UserServiceImpl(userRepository);
        User user = User.builder()
                .firstName("John")
                .lastName("Doe")
                .email("john.doe@example.com")
                .password("SecurePass123!")
                .phoneNumber("081234567890")
                .nik("1234567890123456")
                .preferredLocations(List.of("Jakarta", "Bandung","NY"))
                .skill("Java")
                .price(75)
                .aboutMe("Full-stack developer with 5+ years experience")
                .build();
        userRepository.save(user);

        page = PageRequest.of(0, 1);
    }


    @Test
    void testFilter_SuccessfulMatch() {
        FilterTalentDTO filter = FilterTalentDTO.builder().name("john").preferredLocations( List.of("NY")).skills(List.of("Java")).priceRange(List.of(50.0, 100.0)).build();

        UserResponseWithPagingDTO result = userService.filter(filter,page);
        assertFalse(result.getUsers().isEmpty());
        assertEquals("John", result.getUsers().get(0).getFirstName()); // Case-insensitive [[7]]
        assertEquals(result.getPage(),0);
        assertEquals(result.getSize(), 1);
        assertEquals(result.getTotalPages(), 1);
    }


    @Test
    void testFilter_LocationMismatch() {
        FilterTalentDTO filter = new FilterTalentDTO(null, List.of("CA"), null, null);
        assertThrows(EntityNotFoundException.class, () -> userService.filter(filter,page));
    }

    // Edge Test: Empty price range (ignored) [[6]]
    @Test
    void testFilter_EmptyPriceRange() {
        FilterTalentDTO filter = new FilterTalentDTO(null, null, null, List.of());
        UserResponseWithPagingDTO result = userService.filter(filter,page);
        assertFalse(result.getUsers().isEmpty()); // Price filter skipped
        assertEquals(result.getPage(),0);
        assertEquals(result.getSize(), 1);
        assertEquals(result.getTotalPages(), 1);
    }

    // Edge Test: Exact price boundary [[4]]
    @Test
    void testFilter_ExactPriceBoundary() {
        FilterTalentDTO filter = new FilterTalentDTO(null, null, null, List.of(75.0, 75.0));
        UserResponseWithPagingDTO result = userService.filter(filter, page);
        assertFalse(result.getUsers().isEmpty()); // Exact price match
        assertEquals(result.getPage(),0);
        assertEquals(result.getSize(), 1);
        assertEquals(result.getTotalPages(), 1);
    }

    // Negative Test: Non-existent skill [[4]]
    @Test
    void testFilter_SkillMismatch() {
        FilterTalentDTO filter = new FilterTalentDTO(null, null, List.of("Python"), null);
        assertThrows(EntityNotFoundException.class, () -> userService.filter(filter,page));
    }
    @Test
    void testBlank() {
        FilterTalentDTO filter = new FilterTalentDTO(null, List.of(), List.of(), null);
        UserResponseWithPagingDTO result = userService.filter(filter, page);
        assertFalse(result.getUsers().isEmpty());
        assertEquals(result.getTotalPages(), 1);
    }

    // Edge Test: No filters (empty repository) [[8]]
    @Test
    void testFilter_NoFilters_EmptyRepo() {
        userRepository.deleteAll(); // Clear seeded data
        FilterTalentDTO filter = new FilterTalentDTO();
        assertThrows(EntityNotFoundException.class, () -> userService.filter(filter,page));
    }
}
