package rencanakan.id.talentpool.model;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class CertificateTest {
    private Validator validator;
    private Certificate.CertificateBuilder certificateBuilder;

    @BeforeEach
    void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
        certificateBuilder = Certificate.builder()
                .title("AWS Certified")
                .file("certificate.pdf")
                .user(new User());
    }

    @Test
    void testValidCertificate() {
        Certificate certificate = certificateBuilder.build();
        Set<ConstraintViolation<Certificate>> violations = validator.validate(certificate);
        assertTrue(violations.isEmpty());
    }

    @Test
    void testTitleNotBlank() {
        Certificate certificate = certificateBuilder.title(" ").build();
        Set<ConstraintViolation<Certificate>> violations = validator.validate(certificate);
        assertFalse(violations.isEmpty());
    }

    @Test
    void testFileNotBlank() {
        Certificate certificate = certificateBuilder.file(" ").build();
        Set<ConstraintViolation<Certificate>> violations = validator.validate(certificate);
        assertFalse(violations.isEmpty());
    }

    @Test
    void testUserNotNull() {
        Certificate certificate = certificateBuilder.user(null).build();
        Set<ConstraintViolation<Certificate>> violations = validator.validate(certificate);
        assertFalse(violations.isEmpty());
    }
}
