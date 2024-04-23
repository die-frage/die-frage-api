package com.diefrage.businessserver;

import com.diefrage.businessserver.requests.*;
import com.diefrage.businessserver.services.AnswerService;
import com.diefrage.businessserver.services.StudentService;
import com.diefrage.businessserver.services.SurveyService;
import com.diefrage.businessserver.services.UserService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Objects;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class AnswerApplicationTests {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private UserService userService;

    @Autowired
    private SurveyService surveyService;

    @Autowired
    private AnswerService answerService;

    @Autowired
    private StudentService studentService;

    private String token;
    private Long userId;
    private Long studentId;
    private Long surveyId;

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

        SurveyRequest surveyRequest = new SurveyRequest();
        JSONQuestion question = new JSONQuestion();
        question.setPoints(10);
        question.setQuestion("What is the capital of France?");
        question.setQuestion_id(1);
        question.setType_question("multiple_choice");
        question.setTime_limit_sec(30);
        question.setCorrect_answers(List.of("Paris"));
        question.setIncorrect_answers(Arrays.asList("London", "Berlin", "Madrid"));

        surveyRequest.setTitle("test");
        surveyRequest.setDescription("Test Description");
        surveyRequest.setDate_begin(new Date());
        surveyRequest.setDate_end(new Date(System.currentTimeMillis() + 1000));
        surveyRequest.setMax_students(100);
        surveyRequest.setQuestions(List.of(question));

        surveyId = surveyService.addNewSurvey(userId, surveyRequest).getSurveyId();

        StudentRequest studentRequest = new StudentRequest();
        studentRequest.setEmail("student@student.com");
        studentRequest.setName("student");
        studentRequest.setGroup_number("A1");
        studentId = studentService.register(studentRequest).getStudentId();


        String responseJson = "{\"question_id\": 1, \"responses\": [\"London\"], \"points\": 5}";
        answerService.addAnswerAuthorised(surveyId, studentId, responseJson);
    }

    @AfterEach
    public void deleteAll() throws Exception {
        if (token == null) setup();

        answerService.deleteAnswer(surveyId, studentId);
        studentService.deleteStudent(studentId);
        surveyService.deleteSurvey(userId, surveyId);

        mockMvc.perform(delete("/api/professor/delete/" + userId)
                        .header("Authorization", "Bearer " + this.token)
                        .header("X-Username", "professor@professor.com")
                        .accept(org.springframework.http.MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    void getAllAnswersBySurveyId_IncorrectUser() throws Exception {
        if (token == null) setup();
        mockMvc.perform(get("/api/analyse/survey/{survey_id}/all", surveyId)
                        .param("professor_id", String.valueOf(userId))
                        .header("X-Username", "incorrect@professor.com")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code").value("USER_NOT_FOUND"));
    }

    @Test
    void getAllAnswersBySurveyId_IncorrectSurvey() throws Exception {
        if (token == null) setup();
        mockMvc.perform(get("/api/analyse/survey/{survey_id}/all", (0L))
                        .param("professor_id", String.valueOf(userId))
                        .header("X-Username", "professor@professor.com")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code").value("SURVEY_NOT_FOUND"));
    }

    @Test
    void getAllAnswersBySurveyId_200() throws Exception {
        if (token == null) setup();
        mockMvc.perform(get("/api/analyse/survey/{survey_id}/all", surveyId)
                        .param("professor_id", String.valueOf(userId))
                        .header("X-Username", "professor@professor.com")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.*", hasSize(greaterThan(0))));
    }

    @Test
    void getAnswerOnSurveyByStudent_AnswerNotFound() throws Exception {
        if (token == null) setup();
        mockMvc.perform(get("/api/answer/{survey_id}/student/{student_id}", (surveyId - 1), studentId))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code").value("ANSWER_NOT_FOUND"));
    }

    @Test
    void getAnswerOnSurveyByStudent_StudentNotFound() throws Exception {
        if (token == null) setup();
        mockMvc.perform(get("/api/answer/{survey_id}/student/{student_id}", surveyId, (studentId - 1)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code").value("STUDENT_NOT_FOUND"));
    }

    @Test
    void getAnswerOnSurveyByStudent_Success() throws Exception {
        if (token == null) setup();
        mockMvc.perform(get("/api/answer/{survey_id}/student/{student_id}", surveyId, studentId))
                .andExpect(status().isOk());
    }

    @Test
    void getAnswerOnQuestionByStudent_SurveyNotFound() throws Exception {
        if (token == null) setup();
        Long questionId = 1L;
        mockMvc.perform(get("/api/answer/{survey_id}/student/{student_id}/question/{question_id}", (surveyId - 1), studentId, questionId))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code").value("ANSWER_NOT_FOUND"));
    }

    @Test
    void getAnswerOnQuestionByStudent_StudentNotFound() throws Exception {
        if (token == null) setup();
        Long questionId = 1L;
        mockMvc.perform(get("/api/answer/{survey_id}/student/{student_id}/question/{question_id}", surveyId, (studentId - 1), questionId))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code").value("STUDENT_NOT_FOUND"));
    }

    @Test
    void getAnswerOnQuestionByStudent_QuestionNotFound() throws Exception {
        if (token == null) setup();
        Long questionId = 999L;
        mockMvc.perform(get("/api/answer/{survey_id}/student/{student_id}/question/{question_id}", surveyId, studentId, questionId))
                .andExpect(status().isOk())
                .andExpect(content().string(equalTo("")));
    }

    @Test
    void getAnswerOnQuestionByStudent_Success() throws Exception {
        if (token == null) setup();
        Long questionId = 1L;
        mockMvc.perform(get("/api/answer/{survey_id}/student/{student_id}/question/{question_id}", surveyId, studentId, questionId))
                .andExpect(status().isOk());
    }

    @Test
    void addAnswerOnSurveyByStudent_Success() throws Exception {
        if (token == null) setup();
        String responseJson = "{\"question_id\": 1, \"responses\": [\"Berlin\"], \"points\": 5}";
        mockMvc.perform(post("/api/answer/authorised")
                        .param("survey_id", String.valueOf(surveyId))
                        .param("student_id", String.valueOf(studentId))
                        .param("response", responseJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.answers[0].responses").isArray())
                .andExpect(jsonPath("$.answers[0].responses", hasItem("London")))
                .andExpect(jsonPath("$.answers[0].question_id").value(1))
                .andExpect(jsonPath("$.answers[0].points").value(5));
    }
}
