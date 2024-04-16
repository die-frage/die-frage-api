package com.diefrage.professor;

import jakarta.ws.rs.core.MediaType;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
class ProfessorApplicationTests {

    @Autowired
    private MockMvc mockMvc;

    private final String token = "eyJhbGciOiJIUzI1NiJ9.eyJyb2xlIjoiUk9MRV9VU0VSIiwiaWQiOjEsImVtYWlsIjoidGVzdEB0ZXN0LnJ1Iiwic3ViIjoidGVzdEB0ZXN0LnJ1IiwiaWF0IjoxNzEzMjQ5NDU1LCJleHAiOjE3MTMzOTM0NTV9.A20eDnRgUxwFj2okngrb8GdP6C6qwnfNCw3g5481SiE";

    @Test
    public void testGetProfessorById_WithSameUser() throws Exception {
        mockMvc.perform(get("/api/professor/1").header("X-Username", "test@test.ru")
                        .header("Authorization", "Bearer " + this.token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1)); // Assuming the returned JSON contains the user's id
    }

    @Test
    public void testGetProfessorById_WithDifferentUser() throws Exception {
        mockMvc.perform(get("/api/professor/13").header("X-Username", "test@test.ru")
                        .header("Authorization", "Bearer " + this.token))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code").value("USER_NOT_FOUND"));
    }

    @Test
    public void testGetProfessorById_WithNonExistingUser() throws Exception {
        mockMvc.perform(get("/api/professor/2").header("X-Username", "test@test.ru")
                        .header("Authorization", "Bearer " + this.token))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code").value("USER_NOT_FOUND"));
    }

    @Test
    public void testGetProfessorByEmail_WithSameUser() throws Exception {
        mockMvc.perform(get("/api/professor/by_email/test@test.ru").header("X-Username", "test@test.ru")
                        .header("Authorization", "Bearer " + this.token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1));
    }

    @Test
    public void testGetProfessorByEmail_WithDifferentUser() throws Exception {
        mockMvc.perform(get("/api/professor/by_email/test@test2.ru").header("X-Username", "test@test.ru")
                        .header("Authorization", "Bearer " + this.token))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code").value("USER_NOT_FOUND"));
    }

    @Test
    public void testGetProfessorByEmail_WithNonExistingUser() throws Exception {
        mockMvc.perform(get("/api/professor/by_email/test@test2.ru").header("X-Username", "test@test.ru")
                        .header("Authorization", "Bearer " + this.token))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code").value("USER_NOT_FOUND"));
    }

    @Test
    public void testUpdateCredentials_200() throws Exception {
        Long userId = 1L;
        mockMvc.perform(put("/api/professor/credentials/{id}", userId)
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .param("email", "test@test.ru")
                        .param("password", "123456789")
                        .param("firstName", "string")
                        .param("lastName", "string")
                        .param("patronymic", "string")
                        .header("Authorization", "Bearer " + this.token)
                        .header("X-Username", "test@test.ru"))
                .andExpect(status().isOk());
    }

    @Test
    public void testUpdateCredentials_USER_ALREADY_EXISTS() throws Exception {
        Long userId = 1L;

        mockMvc.perform(put("/api/professor/credentials/{id}", userId)
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .param("email", "test@test2.ru")
                        .param("password", "123456789")
                        .param("firstName", "string")
                        .param("lastName", "string")
                        .param("patronymic", "string")
                        .header("Authorization", "Bearer " + this.token)
                        .header("X-Username", "test@test.ru"))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.code").value("USER_ALREADY_EXISTS"));
    }

    @Test
    public void testUpdateCredentials_INVALID_EMAIL_FORMAT() throws Exception {
        Long userId = 1L;

        mockMvc.perform(put("/api/professor/credentials/{id}", userId)
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .param("email", "test")
                        .param("password", "123456789")
                        .param("firstName", "string")
                        .param("lastName", "string")
                        .param("patronymic", "string")
                        .header("Authorization", "Bearer " + this.token)
                        .header("X-Username", "test@test.ru"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("INVALID_EMAIL_FORMAT"));
    }

    @Test
    public void testUpdateCredentials_INVALID_PASSWORD_FORMAT() throws Exception {
        Long userId = 1L;

        mockMvc.perform(put("/api/professor/credentials/{id}", userId)
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .param("email", "test@test.ru")
                        .param("password", "1234")
                        .param("firstName", "string")
                        .param("lastName", "string")
                        .param("patronymic", "string")
                        .header("Authorization", "Bearer " + this.token)
                        .header("X-Username", "test@test.ru"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("INVALID_PASSWORD_FORMAT"));
    }

    @Test
    public void testUpdateCredentials_INVALID_NAME_FORMAT_FirstName() throws Exception {
        Long userId = 1L;

        mockMvc.perform(put("/api/professor/credentials/{id}", userId)
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .param("email", "test@test.ru")
                        .param("password", "123456789")
                        .param("firstName", "123456789")
                        .param("lastName", "string")
                        .param("patronymic", "string")
                        .header("Authorization", "Bearer " + this.token)
                        .header("X-Username", "test@test.ru"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("INVALID_NAME_FORMAT"));
    }

    @Test
    public void testUpdateCredentials_INVALID_NAME_FORMAT_LastName() throws Exception {
        Long userId = 1L;

        mockMvc.perform(put("/api/professor/credentials/{id}", userId)
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .param("email", "test@test.ru")
                        .param("password", "123456789")
                        .param("firstName", "string")
                        .param("lastName", "123456789")
                        .param("patronymic", "string")
                        .header("Authorization", "Bearer " + this.token)
                        .header("X-Username", "test@test.ru"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("INVALID_NAME_FORMAT"));
    }

    @Test
    public void testUpdateCredentials_INVALID_NAME_FORMAT_Patronymic() throws Exception {
        Long userId = 1L;

        mockMvc.perform(put("/api/professor/credentials/{id}", userId)
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .param("email", "test@test.ru")
                        .param("password", "123456789")
                        .param("firstName", "string")
                        .param("lastName", "string")
                        .param("patronymic", "123456789")
                        .header("Authorization", "Bearer " + this.token)
                        .header("X-Username", "test@test.ru"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("INVALID_NAME_FORMAT"));
    }
}
