package rencanakan.id.talentPool.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.util.Arrays;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class UserProfileTest {

    private UserProfile user;

    @BeforeEach
    void setUp() {
        user = UserProfile.builder()
                .firstName("Fernando")
                .lastName("Valentino")
                .email("fernando@example.com")
                .password("securepassword")
                .phoneNumber("081234567890")
                .address("Jakarta, Indonesia")
                .job("Software Engineer")
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
                .skills(Arrays.asList("Java", "Spring Boot", "Hibernate"))
                .build();

        user.setId(UUID.randomUUID().toString());
    }

    @Test
    void testUserProfileBuilder() {
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
    void testParameterizedUserProfile(String firstName, String lastName, String email) {
        user.setFirstName(firstName);
        user.setLastName(lastName);
        user.setEmail(email);

        assertThat(user.getFirstName()).isEqualTo(firstName);
        assertThat(user.getLastName()).isEqualTo(lastName);
        assertThat(user.getEmail()).isEqualTo(email);
    }

    @Test
    void testEqualsAndHashCode() {
        UserProfile anotherUser = UserProfile.builder()
                .firstName("Fernando")
                .lastName("Valentino")
                .email("fernando@example.com")
                .password("securepassword")
                .phoneNumber("081234567890")
                .address("Jakarta, Indonesia")
                .job("Software Engineer")
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
                .skills(Arrays.asList("Java", "Spring Boot", "Hibernate"))
                .build();

        anotherUser.setId(user.getId());

        assertThat(user).isEqualTo(anotherUser);
        assertThat(user.hashCode()).isEqualTo(anotherUser.hashCode());
    }
}
