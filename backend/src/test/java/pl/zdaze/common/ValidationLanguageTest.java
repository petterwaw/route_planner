package pl.zdaze.common;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * The whole app speaks English: validation messages must not follow the browser's
 * {@code Accept-Language} or the machine's default locale (pl_PL on the dev machine).
 */
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Import(ValidationLanguageTest.SampleController.class)
class ValidationLanguageTest {

    record SampleRequest(@NotBlank String name) {}

    @RestController
    static class SampleController {
        @PostMapping("/validation-sample")
        String create(@Valid @RequestBody SampleRequest request) {
            return request.name();
        }
    }

    @Autowired
    MockMvc mvc;

    @Test
    void validationMessagesAreEnglishEvenForPolishBrowser() throws Exception {
        mvc.perform(post("/validation-sample")
                        .header(HttpHeaders.ACCEPT_LANGUAGE, "pl-PL,pl;q=0.9")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"\"}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("VALIDATION_ERROR"))
                .andExpect(jsonPath("$.detail").value("Invalid input."))
                .andExpect(jsonPath("$.errors[0].field").value("name"))
                .andExpect(jsonPath("$.errors[0].message").value("must not be blank"));
    }
}
