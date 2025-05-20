package rencanakan.id.talentpool.unit.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import rencanakan.id.talentpool.model.User;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;

class UserTest {

   private User user;
   private Validator validator;

   @BeforeEach
   void setUp() {
       user = User.builder()
               .firstName("Fernando")
               .lastName("Valentino")
               .email("fernando@example.com")
               .password("securepassword")
               .phoneNumber("081234567890")
               .photo("profile.jpg")
               .aboutMe("Saya adalah developer")
               .nik("3210123456789123")
               .npwp("012345678910123")
               .photoKtp("ktp.jpg")
               .photoNpwp("npwp.jpg")
               .photoIjazah("ijazah.jpg")
               .experienceYears(5)
               .skkLevel("Level 3")
               .currentLocation("Jakarta")
               .preferredLocations(Arrays.asList("Jakarta", "Bandung", "Surabaya"))
               .skill("Operator")
               .build();

       user.setId(UUID.randomUUID().toString());
       
       ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
       validator = factory.getValidator();
   }

   @Test
   void testUserBuilder() {
       assertThat(user).isNotNull();
       assertThat(user.getId()).isNotNull();
       assertThat(user.getFirstName()).isEqualTo("Fernando");
       assertThat(user.getLastName()).isEqualTo("Valentino");
       assertThat(user.getEmail()).isEqualTo("fernando@example.com");
   }

   @ParameterizedTest
   @CsvSource({
           "Fernando, Valentino, fernando@example.com",
           "Budi, Santoso, budi@example.com",
           "Ayu, Lestari, ayu@example.com"
   })
   void testParameterizedUser(String firstName, String lastName, String email) {
       user.setFirstName(firstName);
       user.setLastName(lastName);
       user.setEmail(email);

       assertThat(user.getFirstName()).isEqualTo(firstName);
       assertThat(user.getLastName()).isEqualTo(lastName);
       assertThat(user.getEmail()).isEqualTo(email);
   }

   @Test
   void testIdNotProvided() {
       User userWithoutId = User.builder()
               .firstName("Test")
               .lastName("User")
               .email("test@example.com")
               .password("securepassword")
               .phoneNumber("081234567890")
               .nik("1234567890123456")
               .build();

       userWithoutId.setId(null);

       assertThat(userWithoutId.getId()).hasSize(36);
       assertThat(userWithoutId.getId()).matches("^[0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12}$");
   }

   @Test
   void testInvalidLastNameFormat() {
       user.setLastName("abbilhaidarfarraszulfikardarifasilkomui2024");
       Set<ConstraintViolation<User>> violations = validator.validate(user);
       assertThat(violations).isNotEmpty();
       assertThat(violations.stream()
               .anyMatch(v -> v.getPropertyPath().toString().equals("lastName")))
               .isTrue();
   }

   @Test
   void testInvalidEmailFormat() {
       user.setEmail("invalid-email-format");
       Set<ConstraintViolation<User>> violations = validator.validate(user);
       assertThat(violations).isNotEmpty();
       assertThat(violations.stream()
               .anyMatch(v -> v.getPropertyPath().toString().equals("email")))
               .isTrue();
   }

   @Test
   void testValidEmailFormat() {
       String validEmail = "test.user+123@example-domain.co.uk";
       user.setEmail(validEmail);
       Set<ConstraintViolation<User>> violations = validator.validate(user);
       assertThat(violations.stream()
               .anyMatch(v -> v.getPropertyPath().toString().equals("email")))
               .isFalse();
       assertEquals(validEmail, user.getEmail());
   }

   @Test
   void testPasswordTooShort() {
       user.setPassword("short");
       Set<ConstraintViolation<User>> violations = validator.validate(user);
       assertThat(violations).isNotEmpty();
       assertThat(violations.stream()
               .anyMatch(v -> v.getPropertyPath().toString().equals("password")))
               .isTrue();
   }

   @Test
   void testValidPasswordFormat() {
       String validPassword = "securepass123";
       user.setPassword(validPassword);
       Set<ConstraintViolation<User>> violations = validator.validate(user);
       assertThat(violations.stream()
               .anyMatch(v -> v.getPropertyPath().toString().equals("password")))
               .isFalse();
       assertEquals(validPassword, user.getPassword());
   }

   @Test
   void testInvalidNikLength() {
       // Test NIK too short
       user.setNik("12345");
       Set<ConstraintViolation<User>> violations = validator.validate(user);
       assertThat(violations).isNotEmpty();
       assertThat(violations.stream()
               .anyMatch(v -> v.getPropertyPath().toString().equals("nik")))
               .isTrue();

       // Test NIK too long
       user.setNik("12345678901234567");
       violations = validator.validate(user);
       assertThat(violations).isNotEmpty();
       assertThat(violations.stream()
               .anyMatch(v -> v.getPropertyPath().toString().equals("nik")))
               .isTrue();
   }

   @Test
   void testValidNikFormat() {
       String validNik = "1234567890123456";
       user.setNik(validNik);
       Set<ConstraintViolation<User>> violations = validator.validate(user);
       assertThat(violations.stream()
               .anyMatch(v -> v.getPropertyPath().toString().equals("nik")))
               .isFalse();
       assertEquals(validNik, user.getNik());
   }

   @Test
   void testSetIdWithNullValue() {
       String initialId = user.getId();
       
       user.setId(null);
       
       assertNotNull(user.getId());
       assertThat(user.getId()).isNotEqualTo(initialId);
       assertThat(user.getId()).hasSize(36);
       assertThat(user.getId()).matches("^[0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12}$");
       
       UUID uuid = UUID.fromString(user.getId());
       assertNotNull(uuid);
   }

   @Test
   void testSetIdWithValue() {
       String testId = "test-id-123";
       user.setId(testId);
       assertEquals(testId, user.getId());
   }

   @Test
   void testDuplicateNIKAndNPWP() {
       User anotherUser = User.builder()
               .nik(user.getNik())
               .npwp(user.getNpwp())
               .build();

       assertThrows(IllegalArgumentException.class, () -> {
           validateUniqueNIKandNPWP(user, anotherUser);
       });
   }

   @Test
   void testSkillManagement() {
       String newSkill = "Operator";
       user.setSkill(newSkill);
       assertEquals(newSkill, user.getSkill());

       String emptySkill = "";
       user.setSkill(emptySkill);
       assertEquals(emptySkill, user.getSkill());
   }

   @Test
   void testPreferredLocationsManagement() {
       // Test setting preferred locations
       List<String> newLocations = Arrays.asList("Bali", "Yogyakarta");
       user.setPreferredLocations(newLocations);
       assertEquals(newLocations, user.getPreferredLocations());
   }

   @Test
   void testSetCurrentLocation() {
       String newLocation = "Bandung";
       user.setCurrentLocation(newLocation);
       assertEquals(newLocation, user.getCurrentLocation());
   }

   @Test
   void testSetPhoneNumber() {
       String newPhone = "087654321098";
       user.setPhoneNumber(newPhone);
       assertEquals(newPhone, user.getPhoneNumber());
   }

   @Test
   void testSetExperienceYears() {
       int newYears = 10;
       user.setExperienceYears(newYears);
       assertEquals(newYears, user.getExperienceYears());
   }

   @Test
   void testSetNpwp() {
       String newNpwp = "123456789012345";
       user.setNpwp(newNpwp);
       assertEquals(newNpwp, user.getNpwp());
   }

   private void validateUniqueNIKandNPWP(User u1, User u2) {
       if (u1.getNik().equals(u2.getNik()) || u1.getNpwp().equals(u2.getNpwp())) {
           throw new IllegalArgumentException("NIK and NPWP must be unique");
       }
   }
}