package com.diefrage.authserver;

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

@SpringBootTest
@AutoConfigureMockMvc
class AuthServerApplicationTests {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void testSignIn() throws Exception {
        String signInRequestJson = "{\"email\":\"string@string.ru\", \"password\":\"123456789\"}";
        mockMvc.perform(post("/auth/sign-in")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(signInRequestJson))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.token").exists());
    }

    @Test
    void testSignUp() throws Exception {
        String signUpRequestJson = "{\"email\":\"testuser" + System.currentTimeMillis() + "@mail.ru\"," +
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
        String token = "eyJhbGciOiJIUzI1NiJ9.eyJyb2xlIjoiUk9MRV9VU0VSIiwiaWQiOjEsImVtYWlsIjoic3RyaW5nQHN0cmluZy5ydSIsInN1YiI6InN0cmluZ0BzdHJpbmcucnUiLCJpYXQiOjE3MTMxMTU0NTUsImV4cCI6MTcxMzI1OTQ1NX0.uh8OcB0qnOjDhKEFbYhD3fH6nXP2deKbb5SuVKBYTJ0";

        mockMvc.perform(get("/auth/validate")
                        .param("token", token))
                .andExpect(status().isOk())
                .andExpect(content().string("string@string.ru"));
    }

    @Test
    void testWrongLogin() throws Exception {
        String signInRequestJson = "{\"email\":\"string1@string.ru\", \"password\":\"123456789\"}";
        mockMvc.perform(post("/auth/sign-in")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(signInRequestJson))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code").value("USER_NOT_FOUND"));
    }

    @Test
    void testWrongPassword() throws Exception {
        String signInRequestJson = "{\"email\":\"string@string.ru\", \"password\":\"0123456789\"}";

        mockMvc.perform(post("/auth/sign-in")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(signInRequestJson))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code").value("USER_NOT_FOUND"));
    }

    @Test
    void testUserAlreadyExists() throws Exception {
        String signUpRequestJson = "{\"email\":\"string@string.ru\"," +
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
