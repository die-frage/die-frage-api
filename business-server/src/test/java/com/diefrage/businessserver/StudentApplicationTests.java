package com.diefrage.businessserver;

import com.diefrage.businessserver.requests.*;
import com.diefrage.businessserver.services.StudentService;
import com.diefrage.businessserver.services.UserService;
import org.json.JSONObject;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;

import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Objects;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class StudentApplicationTests {

    @Autowired
    private MockMvc mockMvc;

    private Long studentId;

    @Autowired
    private StudentService studentService;

    @BeforeEach
    void setup() {
        StudentRequest studentRequest = new StudentRequest();
        studentRequest.setEmail("student@student.com");
        studentRequest.setName("student");
        studentRequest.setGroup_number("A1");
        studentId = studentService.register(studentRequest).getStudentId();
    }

    @AfterEach
    public void deleteAll() throws Exception {
        studentService.deleteStudent(studentId);
    }

    @Test
    @Order(1)
    public void testRegistration() throws Exception {
        String params = "{ \"email\": \"test@test.com\", \"name\": \"student\", \"group_number\": \"A1\" }";

        MvcResult mvcResult = mockMvc.perform(post("/api/student/registration")
                        .content(params)
                        .contentType("application/json"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.student_id").exists())
                .andReturn();

        String responseContent = mvcResult.getResponse().getContentAsString();
        JSONObject jsonResponse = new JSONObject(responseContent);
        String student_id = jsonResponse.getString("student_id");

        mockMvc.perform(delete("/api/student/delete/{student_id}", student_id))
                .andExpect(status().isOk());
    }

    @Test
    @Order(2)
    public void testGetStudentById() throws Exception {
        if (studentId == null) testRegistration();
        mockMvc.perform(get("/api/student/" + studentId))
                .andExpect(status().isOk());
    }

    @Test
    @Order(3)
    public void testGetStudentByEmail() throws Exception {
        if (studentId == null) testRegistration();
        mockMvc.perform(get("/api/student/").param("email", "student@student.com"))
                .andExpect(status().isOk());
    }

    @Test
    @Order(4)
    public void testGetStudentById_StudentNotFound() throws Exception {
        if (studentId == null) testRegistration();
        mockMvc.perform(get("/api/student/0"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code").value("STUDENT_NOT_FOUND"));
    }

    @Test
    @Order(5)
    public void testDeleteStudent_StudentNotFound() throws Exception {
        if (studentId == null) testRegistration();
        mockMvc.perform(delete("/api/student/delete/0"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code").value("STUDENT_NOT_FOUND"));
    }

    @Test
    @Order(6)
    public void testGetStudentByEmail_StudentNotFound() throws Exception {
        if (studentId == null) testRegistration();
        mockMvc.perform(get("/api/student/").param("email", "studentnotfound@example.com"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code").value("STUDENT_NOT_FOUND"));
    }
}
