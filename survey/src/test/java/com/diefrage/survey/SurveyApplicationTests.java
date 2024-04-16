package com.diefrage.survey;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.ws.rs.core.MediaType;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.text.SimpleDateFormat;
import java.util.Date;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class SurveyApplicationTests {

    @Autowired
    private MockMvc mockMvc;

    private final String token = "eyJhbGciOiJIUzI1NiJ9.eyJyb2xlIjoiUk9MRV9VU0VSIiwiaWQiOjEsImVtYWlsIjoidGVzdEB0ZXN0LnJ1Iiwic3ViIjoidGVzdEB0ZXN0LnJ1IiwiaWF0IjoxNzEzMjQ5NDU1LCJleHAiOjE3MTMzOTM0NTV9.A20eDnRgUxwFj2okngrb8GdP6C6qwnfNCw3g5481SiE";

    private Long newSurveyId;

    @Test
    @Order(1)
    public void testGetAllSurveys_withUser_200() throws Exception {
        Long userId = 1L;
        mockMvc.perform(get("/api/survey/{professor_id}/all", userId)
                        .header("X-Username", "test@test.ru")
                        .header("Authorization", "Bearer " + this.token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)));
    }

    @Test
    @Order(2)
    public void testGetAllSurveys_withAnotherUser() throws Exception {
        Long userId = 2L;
        mockMvc.perform(get("/api/survey/{professor_id}/all", userId)
                        .header("X-Username", "test@test.ru")
                        .header("Authorization", "Bearer " + this.token))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code").value("USER_NOT_FOUND"));
    }

    @Test
    @Order(3)
    public void testGetAllSurveys_byName_200() throws Exception {
        Long userId = 1L;
        mockMvc.perform(get("/api/survey/{professor_id}/by_name?survey_name=admin", userId)
                        .header("X-Username", "test@test.ru")
                        .header("Authorization", "Bearer " + this.token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)));
    }

    @Test
    @Order(4)
    public void testGetAllSurveys_byName_empty() throws Exception {
        Long userId = 1L;
        mockMvc.perform(get("/api/survey/{professor_id}/by_name?survey_name=no_test", userId)
                        .header("X-Username", "test@test.ru")
                        .header("Authorization", "Bearer " + this.token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));
    }

    @Test
    @Order(5)
    void testAddNewSurvey() throws Exception {
        String title = "Test Title";
        String description = "Test Description";
        Date dateBegin = new Date();
        String dateBeginFormatted = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSX").format(dateBegin);
        Date dateEnd = new Date(System.currentTimeMillis() + 1000);
        String dateEndFormatted = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSX").format(dateEnd);
        boolean anonymous = true;
        int maxStudents = 100;
        String status = "CREATED_STATUS";
        String questionsValue = "[{\"points\": 10, \"question\": \"What is the capital of France?\"," +
                " \"question_id\": 1, \"type_question\": \"multiple_choice\", \"time_limit_sec\": 30," +
                " \"correct_answers\": [\"Paris\"], \"incorrect_answers\": [\"London\", \"Berlin\", \"Madrid\"]}]";


        String requestBody = String.format("{\"title\":\"%s\",\"description\":\"%s\",\"date_begin\":\"%s\",\"date_end\":\"%s\",\"anonymous\":%s,\"max_students\":%d,\"questions\": %s}",
                title, description, dateBeginFormatted, dateEndFormatted, anonymous, maxStudents, questionsValue);

        Long userId = 1L;
        MvcResult mvcResult = mockMvc.perform(post("/api/survey/{professor_id}/add", userId)
                        .header("X-Username", "test@test.ru")
                        .header("Authorization", "Bearer " + this.token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", notNullValue()))

                .andExpect(jsonPath("$.title", not(emptyString())))
                .andExpect(jsonPath("$.title").value(title))

                .andExpect(jsonPath("$.description", not(emptyString())))
                .andExpect(jsonPath("$.description").value(description))

                .andExpect(jsonPath("$.professor_id", notNullValue()))
                .andExpect(jsonPath("$.professor_id").value(1))

                .andExpect(jsonPath("$.max_students", notNullValue()))
                .andExpect(jsonPath("$.max_students").value(100))

                .andExpect(jsonPath("$.code", not(emptyString())))
                .andExpect(jsonPath("$.link", not(emptyString())))
                .andExpect(jsonPath("$.qr_code", not(emptyString())))
                .andExpect(jsonPath("$.date_begin", not(emptyString())))
                .andExpect(jsonPath("$.date_end", not(emptyString())))

                .andExpect(jsonPath("$.anonymous", notNullValue()))
                .andExpect(jsonPath("$.questions", notNullValue()))

                .andExpect(jsonPath("$.status", notNullValue()))
                .andExpect(jsonPath("$.status.statusId", notNullValue()))
                .andExpect(jsonPath("$.status.name", not(emptyString())))
                .andExpect(jsonPath("$.status.name").value(status))
                .andReturn();

        String content = mvcResult.getResponse().getContentAsString();
        JsonNode jsonNode = new ObjectMapper().readTree(content);
        newSurveyId = jsonNode.get("id").asLong();

    }

    @Test
    @Order(6)
    void testLifeCycle() throws Exception {
        Long professorId = 1L;

        mockMvc.perform(put("/api/survey/{professor_id}/{survey_id}/start", professorId, newSurveyId)
                        .header("X-Username", "test@test.ru")
                        .header("Authorization", "Bearer " + this.token)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status.name").value("STARTED_STATUS"));

        mockMvc.perform(put("/api/survey/{professor_id}/{survey_id}/stop", professorId, newSurveyId)
                        .header("X-Username", "test@test.ru")
                        .header("Authorization", "Bearer " + this.token)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status.name").value("FINISHED_STATUS"));

        mockMvc.perform(put("/api/survey/{professor_id}/{survey_id}/start", professorId, newSurveyId)
                        .header("X-Username", "test@test.ru")
                        .header("Authorization", "Bearer " + this.token)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code").value("SURVEY_NOT_FOUND"));
    }
}
