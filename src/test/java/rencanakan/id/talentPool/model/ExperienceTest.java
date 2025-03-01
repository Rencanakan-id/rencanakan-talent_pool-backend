package rencanakan.id.talentPool.model;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

public class ExperienceTest {

    private static Validator validator;

    @BeforeAll
    public static void setupValidator() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    public void testEmptyConstructor() {
        Experience experience = Experience.builder()
                .build();

        Set<ConstraintViolation<Experience>> violations = validator.validate(experience);
        assertEquals(7, violations.size(), "Expected 7 validation errors");
    }
}