package com.diefrage.authserver;

import com.diefrage.authserver.entities.requests.SignUpRequest;
import com.diefrage.authserver.repositories.UserRepository;
import com.diefrage.authserver.services.AuthenticationService;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.*;
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
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

@SpringBootTest
@AutoConfigureMockMvc
class AuthServerApplicationTests {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private WebApplicationContext context;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private AuthenticationService authenticationService;

    private String tokenTestUser;

    @BeforeEach
    void setup() throws Exception {
        userRepository.deleteAll();
        mockMvc = MockMvcBuilders.webAppContextSetup(context).build();
        SignUpRequest signUpRequest = new SignUpRequest();
        signUpRequest.setEmail("professor@professor.com");
        signUpRequest.setPassword("123456789");
        signUpRequest.setFirstName("Professor");
        signUpRequest.setLastName("Professor");
        signUpRequest.setPatronymic("Professor");
        tokenTestUser = authenticationService.signUp(signUpRequest).getToken();
    }

    @AfterEach
    void cleanup() {
        userRepository.deleteAll();
    }

    @Test
    void testSignIn() throws Exception {
        String signInRequestJson = "{\"email\":\"professor@professor.com\", \"password\":\"123456789\"}";

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
    }

    @Test
    void testSignUp() throws Exception {
        String signUpRequestJson = "{\"email\":\"test" + System.currentTimeMillis() + "@mail.ru\"," +
                " \"password\":\"123456789\"," +
                "\"lastName\": \"professor\"," +
                "\"firstName\": \"professor\"," +
                "\"patronymic\": \"professor\"}";

        mockMvc.perform(post("/auth/sign-up")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(signUpRequestJson))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.token").exists());
    }

    @Test
    void testValidateToken() throws Exception {
        if (tokenTestUser == null) setup();

        mockMvc.perform(get("/auth/validate")
                        .param("token", tokenTestUser))
                .andExpect(status().isOk())
                .andExpect(content().string("professor@professor.com"));
    }

    @Test
    void testWrongLogin() throws Exception {
        String signInRequestJson = "{\"email\":\"wrong@professor.com\", \"password\":\"123456789\"}";
        mockMvc.perform(post("/auth/sign-in")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(signInRequestJson))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code").value("USER_NOT_FOUND"));
    }

    @Test
    void testWrongPassword() throws Exception {
        String signInRequestJson = "{\"email\":\"professor@professor.com\", \"password\":\"0123456789\"}";

        mockMvc.perform(post("/auth/sign-in")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(signInRequestJson))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code").value("USER_NOT_FOUND"));
    }

    @Test
    void testUserAlreadyExists() throws Exception {
        String signUpRequestJson = "{\"email\":\"professor@professor.com\"," +
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
