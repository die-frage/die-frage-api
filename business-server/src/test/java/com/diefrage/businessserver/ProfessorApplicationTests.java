package com.diefrage.businessserver;

import com.diefrage.businessserver.requests.JwtAuthenticationResponse;
import com.diefrage.businessserver.requests.SignUpRequest;
import com.diefrage.businessserver.services.UserService;
import org.junit.jupiter.api.AfterEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Objects;

@SpringBootTest
@AutoConfigureMockMvc
class ProfessorApplicationTests {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private UserService userService;

    private String token;
    private String anotherToken;
    private Long userId;
    private Long anotherUserId;

    @BeforeEach
    void setup() {
        String url = "http://localhost:8787/auth/sign-up";
        SignUpRequest request = new SignUpRequest();
        request.setEmail("professor@professor.com");
        request.setPatronymic("professor");
        request.setFirstName("professor");
        request.setLastName("professor");
        request.setPassword("123456789");

        HttpEntity<SignUpRequest> requestEntity = new HttpEntity<>(request, new HttpHeaders());

        ResponseEntity<JwtAuthenticationResponse> responseEntity = restTemplate.exchange(
                url,
                HttpMethod.POST,
                requestEntity,
                JwtAuthenticationResponse.class
        );

        token = Objects.requireNonNull(responseEntity.getBody()).getToken();
        userId = userService.getUserByEmail("professor@professor.com").getId();

        request = new SignUpRequest();
        request.setEmail("professor2@professor.com");
        request.setPatronymic("professor");
        request.setFirstName("professor");
        request.setLastName("professor");
        request.setPassword("123456789");

        requestEntity = new HttpEntity<>(request, new HttpHeaders());
        responseEntity = restTemplate.exchange(
                url,
                HttpMethod.POST,
                requestEntity,
                JwtAuthenticationResponse.class
        );

        anotherToken = Objects.requireNonNull(responseEntity.getBody()).getToken();
        anotherUserId = userService.getUserByEmail("professor2@professor.com").getId();
    }

    @Test
    public void testGetProfessorById_WithSameUser() throws Exception {
        if (token == null) setup();
        mockMvc.perform(get("/api/professor/" + userId)
                        .header("X-Username", "professor@professor.com")
                        .header("Authorization", "Bearer " + this.token)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(userId));
    }

    @Test
    public void testGetProfessorById_WithDifferentUser() throws Exception {
        if (token == null) setup();
        mockMvc.perform(get("/api/professor/" + userId)
                        .header("X-Username", "different@professor.com")
                        .header("Authorization", "Bearer " + this.token)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code").value("USER_NOT_FOUND"));
    }

    @Test
    public void testGetProfessorById_WithNonExistingUser() throws Exception {
        if (token == null) setup();
        mockMvc.perform(get("/api/professor/0")
                        .header("X-Username", "professor@professor.com")
                        .header("Authorization", "Bearer " + this.token)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code").value("USER_NOT_FOUND"));
    }

    @Test
    public void testGetProfessorByEmail_WithSameUser() throws Exception {
        if (token == null) setup();
        mockMvc.perform(get("/api/professor/by_email/professor@professor.com")
                        .header("X-Username", "professor@professor.com")
                        .header("Authorization", "Bearer " + this.token)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(this.userId));
    }

    @Test
    public void testGetProfessorByEmail_WithDifferentUser() throws Exception {
        if (token == null) setup();
        mockMvc.perform(get("/api/professor/by_email/professor2@professor.com")
                        .header("X-Username", "professor@professor.com")
                        .header("Authorization", "Bearer " + this.token)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code").value("USER_NOT_FOUND"));
    }

    @Test
    public void testGetProfessorByEmail_WithNonExistingUser() throws Exception {
        if (token == null) setup();
        mockMvc.perform(get("/api/professor/by_email/nonexiting@professor.com")
                        .header("X-Username", "professor2@professor.com")
                        .header("Authorization", "Bearer " + this.token)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code").value("USER_NOT_FOUND"));
    }

    @Test
    public void testUpdateCredentials_200() throws Exception {
        if (token == null) setup();
        mockMvc.perform(put("/api/professor/credentials/" + userId)
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .param("email", "professor@professor.com")
                        .param("password", "123456789")
                        .param("firstName", "string")
                        .param("lastName", "string")
                        .param("patronymic", "string")
                        .header("Authorization", "Bearer " + this.token)
                        .header("X-Username", "professor@professor.com")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    public void testUpdateCredentials_USER_ALREADY_EXISTS() throws Exception {
        if (token == null) setup();
        mockMvc.perform(put("/api/professor/credentials/" + userId)
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .param("email", "professor2@professor.com")
                        .param("password", "123456789")
                        .param("firstName", "string")
                        .param("lastName", "string")
                        .param("patronymic", "string")
                        .header("Authorization", "Bearer " + this.token)
                        .header("X-Username", "professor@professor.com")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.code").value("USER_ALREADY_EXISTS"));
    }

    @Test
    public void testUpdateCredentials_INVALID_EMAIL_FORMAT() throws Exception {
        if (token == null) setup();
        mockMvc.perform(put("/api/professor/credentials/" + userId)
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .param("email", "professor")
                        .param("password", "123456789")
                        .param("firstName", "string")
                        .param("lastName", "string")
                        .param("patronymic", "string")
                        .header("Authorization", "Bearer " + this.token)
                        .header("X-Username", "professor@professor.com")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("INVALID_EMAIL_FORMAT"));
    }

    @Test
    public void testUpdateCredentials_INVALID_PASSWORD_FORMAT() throws Exception {
        if (token == null) setup();
        mockMvc.perform(put("/api/professor/credentials/" + userId)
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .param("email", "professor@professor.com")
                        .param("password", "1234")
                        .param("firstName", "string")
                        .param("lastName", "string")
                        .param("patronymic", "string")
                        .header("Authorization", "Bearer " + this.token)
                        .header("X-Username", "professor@professor.com")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("INVALID_PASSWORD_FORMAT"));
    }

    @Test
    public void testUpdateCredentials_INVALID_NAME_FORMAT_FirstName() throws Exception {
        if (token == null) setup();
        mockMvc.perform(put("/api/professor/credentials/" + userId)
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .param("email", "professor@professor.com")
                        .param("password", "123456789")
                        .param("firstName", "1")
                        .param("lastName", "string")
                        .param("patronymic", "string")
                        .header("Authorization", "Bearer " + this.token)
                        .header("X-Username", "professor@professor.com")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("INVALID_NAME_FORMAT"));
    }

    @Test
    public void testUpdateCredentials_INVALID_NAME_FORMAT_LastName() throws Exception {
        if (token == null) setup();
        mockMvc.perform(put("/api/professor/credentials/" + userId)
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .param("email", "professor@professor.com")
                        .param("password", "123456789")
                        .param("firstName", "string")
                        .param("lastName", "1")
                        .param("patronymic", "string")
                        .header("Authorization", "Bearer " + this.token)
                        .header("X-Username", "professor@professor.com")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("INVALID_NAME_FORMAT"));
    }

    @Test
    public void testUpdateCredentials_INVALID_NAME_FORMAT_Patronymic() throws Exception {
        if (token == null) setup();
        mockMvc.perform(put("/api/professor/credentials/" + userId)
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .param("email", "professor@professor.com")
                        .param("password", "123456789")
                        .param("firstName", "string")
                        .param("lastName", "string")
                        .param("patronymic", "1")
                        .header("Authorization", "Bearer " + this.token)
                        .header("X-Username", "professor@professor.com")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("INVALID_NAME_FORMAT"));
    }


    @AfterEach
    public void deleteAll() throws Exception {
        if (token == null) setup();
        if (anotherToken == null) setup();

        mockMvc.perform(delete("/api/professor/delete/" + userId)
                        .header("Authorization", "Bearer " + this.token)
                        .header("X-Username", "professor@professor.com")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        mockMvc.perform(delete("/api/professor/delete/" + anotherUserId)
                        .header("Authorization", "Bearer " + this.anotherToken)
                        .header("X-Username", "professor2@professor.com")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }
}
