package pl.zdaze.common;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.json.JsonCompareMode;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest(properties = "app.clock.fixed=2026-11-17T15:40:00+01:00")
@AutoConfigureMockMvc
@ActiveProfiles("test")
class HealthControllerTest {

    @Autowired
    MockMvc mvc;

    @Test
    void healthReturnsStatusServerTimeFromTheClockAndProviders() throws Exception {
        mvc.perform(get("/api/health"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(content().json("""
                                {
                                  "status": "ok",
                                  "now": "2026-11-17T15:40:00+01:00",
                                  "routingProvider": "mock",
                                  "aiProvider": "mock"
                                }
                                """, JsonCompareMode.STRICT));
    }
}
