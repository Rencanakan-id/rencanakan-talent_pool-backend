package rencanakan.id.talentpool.dto;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import rencanakan.id.talentpool.enums.StatusType;

import java.util.Set;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class RecommendationStatusRequestDTOTest {

    private static Validator validator;

    @BeforeAll
    static void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }
    @Test
    void testAllArgsConstructor() {

        RecommendationStatusRequestDTO dto = new RecommendationStatusRequestDTO(StatusType.ACCEPTED);

        assertThat(dto.getStatus()).isEqualTo(StatusType.ACCEPTED);
    }

    @Test
    void testGetterAndSetter() {

        RecommendationStatusRequestDTO dto = new RecommendationStatusRequestDTO(null);

        dto.setStatus(StatusType.PENDING);

        assertThat(dto.getStatus()).isEqualTo(StatusType.PENDING);
    }

    @Test
    void testValidStatus() {
        RecommendationStatusRequestDTO dto = new RecommendationStatusRequestDTO(StatusType.ACCEPTED);

        Set<ConstraintViolation<RecommendationStatusRequestDTO>> violations = validator.validate(dto);

        assertTrue(violations.isEmpty(), "There should be no validation errors");
    }

    @Test
    void testNullStatusShouldFail() {
        RecommendationStatusRequestDTO dto = new RecommendationStatusRequestDTO(null);

        Set<ConstraintViolation<RecommendationStatusRequestDTO>> violations = validator.validate(dto);

        assertEquals(1, violations.size());
        assertEquals("Status is required", violations.iterator().next().getMessage());
    }

}
