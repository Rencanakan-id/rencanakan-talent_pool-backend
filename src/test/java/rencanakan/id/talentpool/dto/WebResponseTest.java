package rencanakan.id.talentpool.dto;

import org.junit.jupiter.api.Test;

import rencanakan.id.talentpool.dto.WebResponse;

import static org.junit.jupiter.api.Assertions.*;


class WebResponseTest {
    @Test
    void testWebResponseBuilder(){
        WebResponse<String> response = WebResponse.<String>builder().data("OK").errors("error").build();
        assertEquals("OK", response.getData());
        assertEquals("error", response.getErrors());
    }

    @Test
    void testWebResponseToString(){
        assertNotNull(WebResponse.<String>builder().data("Oke").errors("error").toString());
    }

}