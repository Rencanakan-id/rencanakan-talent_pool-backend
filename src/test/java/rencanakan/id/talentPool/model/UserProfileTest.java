package rencanakan.id.talentPool.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

public class UserProfileTest {

    private UserProfile user;

    @BeforeEach
    void setUp() {
        UserProfile newProfile = new UserProfile("1", "John", "Doe", "012345678910", "Jakarta", "Jabodetabek", "Arsitektur");
        this.user = newProfile;
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

}