package rencanakan.id.talentPool.dto;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;


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