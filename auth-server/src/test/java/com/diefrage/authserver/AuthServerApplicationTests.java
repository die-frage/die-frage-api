package com.diefrage.authserver;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

@SpringBootTest
@AutoConfigureMockMvc
class AuthServerApplicationTests {

    @Autowired
    private MockMvc mockMvc;

    private String tokenTestUser;

    @Test
    void testSignIn() throws Exception {
        String signInRequestJson = "{\"email\":\"test@test.ru\", \"password\":\"123456789\"}";

        MvcResult result = mockMvc.perform(post("/auth/sign-in")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(signInRequestJson))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.token").exists())
                .andReturn();

        String jsonResponse = result.getResponse().getContentAsString();
        JsonNode jsonNode = new ObjectMapper().readTree(jsonResponse);
        tokenTestUser = jsonNode.get("token").asText();
        System.out.println(tokenTestUser);
    }

    @Test
    void testSignUp() throws Exception {
        String signUpRequestJson = "{\"email\":\"test" + System.currentTimeMillis() + "@mail.ru\"," +
                " \"password\":\"testpassword\"," +
                "\"lastName\": \"test\"," +
                "\"firstName\": \"test\"," +
                "\"patronymic\": \"test\"}";

        mockMvc.perform(post("/auth/sign-up")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(signUpRequestJson))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.token").exists());
    }

    @Test
    void testValidateToken() throws Exception {
        if (tokenTestUser == null) testSignIn();

        mockMvc.perform(get("/auth/validate")
                        .param("token", tokenTestUser))
                .andExpect(status().isOk())
                .andExpect(content().string("test@test.ru"));
    }

    @Test
    void testWrongLogin() throws Exception {
        String signInRequestJson = "{\"email\":\"testtest@test.ru\", \"password\":\"123456789\"}";
        mockMvc.perform(post("/auth/sign-in")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(signInRequestJson))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code").value("USER_NOT_FOUND"));
    }

    @Test
    void testWrongPassword() throws Exception {
        String signInRequestJson = "{\"email\":\"test@test.ru\", \"password\":\"0123456789\"}";

        mockMvc.perform(post("/auth/sign-in")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(signInRequestJson))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code").value("USER_NOT_FOUND"));
    }

    @Test
    void testUserAlreadyExists() throws Exception {
        String signUpRequestJson = "{\"email\":\"test@test.ru\"," +
                " \"password\":\"testpassword\"," +
                "\"lastName\": \"test\"," +
                "\"firstName\": \"test\"," +
                "\"patronymic\": \"test\"}";

        mockMvc.perform(post("/auth/sign-up")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(signUpRequestJson))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.code").value("USER_ALREADY_EXISTS"));
    }

    @Test
    void testInvalidNameFormat() throws Exception {
        String signUpRequestJson = "{\"email\":\"testuser" + System.currentTimeMillis() + "@mail.ru\"," +
                " \"password\":\"testpassword\"," +
                "\"lastName\": \"test_\"," +
                "\"firstName\": \"test_\"," +
                "\"patronymic\": \"test_\"}";

        mockMvc.perform(post("/auth/sign-up")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(signUpRequestJson))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("INVALID_NAME_FORMAT"));
    }
}
