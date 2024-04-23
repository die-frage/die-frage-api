package com.diefrage.businessserver;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class AnswerApplicationTests {

    @Autowired
    private MockMvc mockMvc;

    private final String token = "eyJhbGciOiJIUzI1NiJ9.eyJyb2xlIjoiUk9MRV9VU0VSIiwiaWQiOjEsImVtYWlsIjoidGVzd" +
            "EB0ZXN0LnJ1Iiwic3ViIjoidGVzdEB0ZXN0LnJ1IiwiaWF0IjoxNzEzMjQ5NDU1LCJleHAiOjE3MTMzOTM0NTV9.A20eDnR" +
            "gUxwFj2okngrb8GdP6C6qwnfNCw3g5481SiE";

    @Test
    void getAllAnswersBySurveyId_IncorrectUser() throws Exception {
        Long surveyId = 1L;
        mockMvc.perform(get("/api/analyse/survey/{survey_id}/all", surveyId)
                        .param("professor_id", "1")
                        .header("X-Username", "notfound@test.ru")
                        .header("Authorization", "Bearer " + this.token))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code").value("SURVEY_NOT_FOUND"));
    }

    @Test
    void getAllAnswersBySurveyId_IncorrectSurvey() throws Exception {
        Long surveyId = 0L;
        mockMvc.perform(get("/api/analyse/survey/{survey_id}/all", surveyId)
                        .param("professor_id", "1")
                        .header("X-Username", "test@test.ru")
                        .header("Authorization", "Bearer " + this.token))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code").value("SURVEY_NOT_FOUND"));
    }

    @Test
    void getAllAnswersBySurveyId_200() throws Exception {
        Long surveyId = 1L;
        mockMvc.perform(get("/api/analyse/survey/{survey_id}/all", surveyId)
                        .param("professor_id", "1")
                        .header("X-Username", "test@test.ru")
                        .header("Authorization", "Bearer " + this.token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.*", hasSize(greaterThan(0))));
    }

    @Test
    void getAnswerOnSurveyByStudent_SurveyNotFound() throws Exception {
        Long surveyId = 999L; // Survey ID that doesn't exist
        Long studentId = 1L; // Student ID
        mockMvc.perform(get("/api/answer/{survey_id}/student/{student_id}", surveyId, studentId))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code").value("ANSWER_NOT_FOUND"));
    }

    @Test
    void getAnswerOnSurveyByStudent_StudentNotFound() throws Exception {
        Long surveyId = 1L; // Survey ID
        Long studentId = 999L; // Student ID that doesn't exist
        mockMvc.perform(get("/api/answer/{survey_id}/student/{student_id}", surveyId, studentId))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code").value("STUDENT_NOT_FOUND"));
    }

    @Test
    void getAnswerOnSurveyByStudent_Success() throws Exception {
        Long surveyId = 1L; // Survey ID
        Long studentId = 1L; // Student ID
        mockMvc.perform(get("/api/answer/{survey_id}/student/{student_id}", surveyId, studentId))
                .andExpect(status().isOk());
    }

    @Test
    void getAnswerOnQuestionByStudent_SurveyNotFound() throws Exception {
        Long surveyId = 999L; // Survey ID that doesn't exist
        Long studentId = 1L; // Student ID
        Long questionId = 1L; // Question ID
        mockMvc.perform(get("/api/answer/{survey_id}/student/{student_id}/question/{question_id}", surveyId, studentId, questionId))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code").value("ANSWER_NOT_FOUND"));
    }

    @Test
    void getAnswerOnQuestionByStudent_StudentNotFound() throws Exception {
        Long surveyId = 1L; // Survey ID
        Long studentId = 999L; // Student ID that doesn't exist
        Long questionId = 1L; // Question ID
        mockMvc.perform(get("/api/answer/{survey_id}/student/{student_id}/question/{question_id}", surveyId, studentId, questionId))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code").value("STUDENT_NOT_FOUND"));
    }

    @Test
    void getAnswerOnQuestionByStudent_QuestionNotFound() throws Exception {
        Long surveyId = 1L;
        Long studentId = 1L;
        Long questionId = 999L;
        mockMvc.perform(get("/api/answer/{survey_id}/student/{student_id}/question/{question_id}", surveyId, studentId, questionId))
                .andExpect(status().isOk())
                .andExpect(content().string(equalTo("")));
    }

    @Test
    void getAnswerOnQuestionByStudent_Success() throws Exception {
        Long surveyId = 1L;
        Long studentId = 1L;
        Long questionId = 1L;
        mockMvc.perform(get("/api/answer/{survey_id}/student/{student_id}/question/{question_id}", surveyId, studentId, questionId))
                .andExpect(status().isOk());
    }

    @Test
    void addAnswerOnSurveyByStudent_Success() throws Exception {
        Long surveyId = 1L;
        Long studentId = 1L;
        String responseJson = "{\"question_id\": 1, \"responses\": [\"A1\", \"A2\"], \"points\": 5}";
        mockMvc.perform(post("/api/answer/authorised")
                        .param("survey_id", surveyId.toString())
                        .param("student_id", studentId.toString())
                        .param("response", responseJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.answers[0].responses").isArray())
                .andExpect(jsonPath("$.answers[0].responses", hasItem("A1")))
                .andExpect(jsonPath("$.answers[0].responses", hasItem("A2")))
                .andExpect(jsonPath("$.answers[0].question_id").value(1))
                .andExpect(jsonPath("$.answers[0].points").value(5));
    }

    @Test
    void addAnswerOnSurveyByAnonymous_Success() throws Exception {
        Long surveyId = 1L;
        String responseJson = "{\"question_id\": 1, \"responses\": [\"A1\", \"A2\"], \"points\": 5}";
        mockMvc.perform(post("/api/answer/anonymous")
                        .param("survey_id", surveyId.toString())
                        .param("response", responseJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.answers[0].responses").isArray())
                .andExpect(jsonPath("$.answers[0].responses", hasItem("A1")))
                .andExpect(jsonPath("$.answers[0].responses", hasItem("A2")))
                .andExpect(jsonPath("$.answers[0].question_id").value(1))
                .andExpect(jsonPath("$.answers[0].points").value(5));
    }
}
