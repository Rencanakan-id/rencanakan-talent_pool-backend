//package rencanakan.id.talentPool.repository;
//
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//
//import java.util.*;
//
//import static org.junit.jupiter.api.Assertions.*;
//
//import rencanakan.id.talentPool.model.UserProfile;
//
//public class UserProfileRepositoryTest {
//
//    UserProfileRepository repo;
//    UserProfile user;
//    UserProfile newProfile;
//
//    @BeforeEach
//    void setUp() {
//        repo = new UserProfileRepository();
//        user = new UserProfile();
//        user.setId("eb558e9f-1c39-460e-8860-71af6af63bd6");
//        user.setFirstName("John");
//        user.setLastName("Doe");
//        user.setPhoneNumber("012345678910");
//        user.setDomisili("Bekasi");
//        user.setAvailLocation("Depok");
//        user.setOccupation("Arsitektur");
//
//        newProfile = new UserProfile();
//        user.setId("eb558e9f-1c39-460e-8860-71af6af63bd6");
//        user.setFirstName("James");
//        user.setLastName("Smith");
//        user.setPhoneNumber("08123456789");
//        user.setDomisili("Jakarta");
//        user.setAvailLocation("Tangerang");
//        user.setOccupation("Ahli Bangunan");
//    }
//
//    @Test
//    void editValidUserProfileTest() {
//        UserProfile editedProfile = repo.editProfile(user, newProfile);
//
//        assertEquals(newProfile.getFirstName(), editedProfile.getFirstName());
//        assertEquals(newProfile.getLastName(), editedProfile.getLastName());
//        assertEquals(newProfile.getPhoneNumber(), editedProfile.getPhoneNumber());
//        assertEquals(newProfile.getDomisili(), editedProfile.getDomisili());
//        assertEquals(newProfile.getAvailLocation(), editedProfile.getAvailLocation());
//        assertEquals(newProfile.getOccupation(), editedProfile.getOccupation());
//    }
//
//    @Test
//    void editInvalidPhoneNumberTest() {
//        newProfile.setPhoneNumber("A&12341B923");
//
//        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
//            repo.editProfile(user, newProfile);
//        });
//
//        assertTrue(exception.getMessage().contains("Invalid phone number format"));
//        assertEquals("012345678910", user.getPhoneNumber());
//    }
//
//    @Test
//    void editEmptyFirstNameTest() {
//        newProfile.setFirstName("");
//
//        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
//            repo.editProfile(user, newProfile);
//        });
//
//        assertTrue(exception.getMessage().contains("First name cannot be empty"));
//        assertEquals("John", user.getFirstName());
//    }
//
//    @Test
//    void editNullValuesTest() {
//        newProfile.setFirstName(null);
//
//        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
//            repo.editProfile(user, newProfile);
//        });
//
//        assertTrue(exception.getMessage().contains("First name cannot be null"));
//    }
//
//    @Test
//    void editExtremelyLongInputTest() {
//        String veryLongName = "A".repeat(1000);
//        newProfile.setFirstName(veryLongName);
//
//        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
//            repo.editProfile(user, newProfile);
//        });
//
//        assertTrue(exception.getMessage().contains("First name exceeds maximum length"));
//    }
//
//    @Test
//    void editSpecialCharactersTest() {
//        newProfile.setFirstName("John-Kärl Müller");
//        newProfile.setLastName("O'Brien-Smith");
//
//        UserProfile editedProfile = repo.editProfile(user, newProfile);
//
//        assertEquals("John-Kärl Müller", editedProfile.getFirstName());
//        assertEquals("O'Brien-Smith", editedProfile.getLastName());
//    }
//
//}
