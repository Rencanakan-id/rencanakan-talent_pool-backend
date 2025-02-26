package rencanakan.id.talentPool.repository;

import rencanakan.id.talentPool.model.UserProfile;
import java.util.regex.Pattern;

public class UserProfileRepository {

    // Regular expression for validating phone numbers
    private static final Pattern PHONE_PATTERN = Pattern.compile("^[+]?[0-9\\-\\s]{7,15}$");

    // Maximum length for text fields
    private static final int MAX_NAME_LENGTH = 100;
    private static final int MAX_LOCATION_LENGTH = 100;
    private static final int MAX_OCCUPATION_LENGTH = 100;

    public UserProfile editProfile(UserProfile user, UserProfile newProfile) {
       return  null;
    }
}