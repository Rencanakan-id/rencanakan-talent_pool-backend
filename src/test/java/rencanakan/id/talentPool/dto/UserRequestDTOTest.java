package rencanakan.id.talentPool.dto;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class UserRequestDTOTest {

    private Validator validator;

    @BeforeEach
    void setUp() {
        try (ValidatorFactory factory = Validation.buildDefaultValidatorFactory()) {
            validator = factory.getValidator();
        }
    }

    @Test
    void testValidUserProfileRequestDTO() {
        UserProfileRequestDTO dto = new UserProfileRequestDTO();

        dto.setFirstName("John");
        dto.setLastName("Doe");
        dto.setEmail("john.doe@example.com");
        dto.setPhoneNumber("1234567890");
        dto.setAddress("Jalan Margonda");
        dto.setJob("Arsitek");
        dto.setAboutMe("Hola");
        dto.setNik("1234567890123456");
        dto.setNpwp("123456789");
        dto.setExperienceYears(5);
        dto.setSkkLevel("Profesional");
        dto.setCurrentLocation("Jakarta");
        dto.setPreferredLocations(List.of("Bali", "Bandung"));
        dto.setSkill("Arsitektur");

        Set<ConstraintViolation<UserProfileRequestDTO>> violations = validator.validate(dto);
        assertTrue(violations.isEmpty(), "Expected no validation errors");
    }

    @Test
    void testInvalidUserProfileRequestDTO() {
        UserProfileRequestDTO dto = new UserProfileRequestDTO();

        Set<ConstraintViolation<UserProfileRequestDTO>> violations = validator.validate(dto);
        assertFalse(violations.isEmpty(), "Expected validation errors due to missing fields");
    }
}
