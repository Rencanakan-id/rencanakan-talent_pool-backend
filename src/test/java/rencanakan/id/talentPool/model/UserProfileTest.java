package rencanakan.id.talentPool.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

public class UserProfileTest {

    private UserProfile user;
    private List<UserProfile> listUser;

    @BeforeEach
    void setUp() {
        UserProfile newProfile = new UserProfile("1", "John", "Doe", "012345678910", "Jakarta", "Jabodetabek", "Arsitektur");
        this.user = newProfile;
        this.listUser.add(newProfile);
    }

    // Test for retrieve user profile information
    
    @Test
    void getUserFirstName() {
        String userFirstName = user.getFirstName();
        assertEquals("John", userFirstName);
    }
    @Test
    void getUserLastName() {
        String userLastName = user.getLastName();
        assertEquals("Doe", userLastName);
    }

    @Test
    void getUserPhoneNumber() {
        String userPhoneNumber = user.getPhoneNumber();
        assertEquals("012345678910", userPhoneNumber);
    }

    @Test
    void getUserDomisili() {
        String userDomisili = user.getDomisili();
        assertEquals("Jakarta", userDomisili);
    }

    @Test
    void getUserAvailLocation() {
        String userAvailLocation = user.getAvailLocation();
        assertEquals("Jabodetabek", userAvailLocation);
    }

    @Test
    void getUserOccupation() {
        String userOccupation = user.getOccupation();
        assertEquals("Arsitektur", userOccupation);
    }

    // Test for edit profile information

    @Test
    void editUserProfileTest() {
        String newFirstName = "Jane";
        String newLastName = "Smith";
        String newPhoneNumber = "0987654321";
        String newDomisili = "Bandung";
        String newAvailLocation = "Jawa Barat";
        String newOccupation = "Interior Design";

        user.setFirstName(newFirstName);
        user.setLastName(newLastName);
        user.setPhoneNumber(newPhoneNumber);
        user.setDomisili(newDomisili);
        user.setAvailLocation(newAvailLocation);
        user.setOccupation(newOccupation);

        assertEquals(newFirstName, user.getFirstName());
        assertEquals(newLastName, user.getLastName());
        assertEquals(newPhoneNumber, user.getPhoneNumber());
        assertEquals(newDomisili, user.getDomisili());
        assertEquals(newAvailLocation, user.getAvailLocation());
        assertEquals(newOccupation, user.getOccupation());
    }


}