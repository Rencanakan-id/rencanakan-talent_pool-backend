package rencanakan.id.talentPool.dto;

import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class UserProfileResponseDTOTest {

    @Test
    void testNoArgsConstructor() {
        UserProfileResponseDTO dto = new UserProfileResponseDTO();
        assertNotNull(dto);
        assertNull(dto.getId());
        assertNull(dto.getFirstName());
        assertNull(dto.getLastName());
    }

    @Test
    void testAllArgsConstructor() {
        String id = "test-id";
        String firstName = "John";
        String lastName = "Doe";
        String email = "john@example.com";
        String phoneNumber = "1234567890";
        String address = "123 Main St";
        String job = "Developer";
        String photo = "photo.jpg";
        String aboutMe = "About me text";
        String nik = "1234567890123456";
        String npwp = "12.345.678.9-012.345";
        String photoKtp = "ktp.jpg";
        String photoNpwp = "npwp.jpg";
        String photoIjazah = "ijazah.jpg";
        Integer experienceYears = 5;
        String skkLevel = "Intermediate";
        String currentLocation = "Jakarta";
        List<String> preferredLocations = Arrays.asList("Jakarta", "Bandung");
        String skill = "Operator";

        UserProfileResponseDTO dto = new UserProfileResponseDTO(
                id, firstName, lastName, email, phoneNumber, address, job,
                photo, aboutMe, nik, npwp, photoKtp, photoNpwp, photoIjazah,
                experienceYears, skkLevel, currentLocation, preferredLocations, skill);

        assertEquals(id, dto.getId());
        assertEquals(firstName, dto.getFirstName());
        assertEquals(lastName, dto.getLastName());
        assertEquals(email, dto.getEmail());
        assertEquals(phoneNumber, dto.getPhoneNumber());
        assertEquals(address, dto.getAddress());
        assertEquals(job, dto.getJob());
        assertEquals(photo, dto.getPhoto());
        assertEquals(aboutMe, dto.getAboutMe());
        assertEquals(nik, dto.getNik());
        assertEquals(npwp, dto.getNpwp());
        assertEquals(photoKtp, dto.getPhotoKtp());
        assertEquals(photoNpwp, dto.getPhotoNpwp());
        assertEquals(photoIjazah, dto.getPhotoIjazah());
        assertEquals(experienceYears, dto.getExperienceYears());
        assertEquals(skkLevel, dto.getSkkLevel());
        assertEquals(currentLocation, dto.getCurrentLocation());
        assertEquals(preferredLocations, dto.getPreferredLocations());
        assertEquals(skill, dto.getSkill());
    }

    @Test
    void testGettersAndSetters() {
        UserProfileResponseDTO dto = new UserProfileResponseDTO();
        
        dto.setId("test-id");
        dto.setFirstName("John");
        dto.setLastName("Doe");
        dto.setEmail("john@example.com");
        dto.setPhoneNumber("1234567890");
        dto.setAddress("123 Main St");
        dto.setJob("Developer");
        dto.setPhoto("photo.jpg");
        dto.setAboutMe("About me text");
        dto.setNik("1234567890123456");
        dto.setNpwp("12.345.678.9-012.345");
        dto.setPhotoKtp("ktp.jpg");
        dto.setPhotoNpwp("npwp.jpg");
        dto.setPhotoIjazah("ijazah.jpg");
        dto.setExperienceYears(5);
        dto.setSkkLevel("Intermediate");
        dto.setCurrentLocation("Jakarta");
        List<String> locations = Arrays.asList("Jakarta", "Bandung");
        dto.setPreferredLocations(locations);
        dto.setSkill("Operator");

        assertEquals("test-id", dto.getId());
        assertEquals("John", dto.getFirstName());
        assertEquals("Doe", dto.getLastName());
        assertEquals("john@example.com", dto.getEmail());
        assertEquals("1234567890", dto.getPhoneNumber());
        assertEquals("123 Main St", dto.getAddress());
        assertEquals("Developer", dto.getJob());
        assertEquals("photo.jpg", dto.getPhoto());
        assertEquals("About me text", dto.getAboutMe());
        assertEquals("1234567890123456", dto.getNik());
        assertEquals("12.345.678.9-012.345", dto.getNpwp());
        assertEquals("ktp.jpg", dto.getPhotoKtp());
        assertEquals("npwp.jpg", dto.getPhotoNpwp());
        assertEquals("ijazah.jpg", dto.getPhotoIjazah());
        assertEquals(5, dto.getExperienceYears());
        assertEquals("Intermediate", dto.getSkkLevel());
        assertEquals("Jakarta", dto.getCurrentLocation());
        assertEquals(locations, dto.getPreferredLocations());
        assertEquals("Operator", dto.getSkill());
    }
}
