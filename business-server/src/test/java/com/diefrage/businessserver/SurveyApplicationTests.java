package com.diefrage.businessserver;

import com.diefrage.businessserver.requests.JSONQuestion;
import com.diefrage.businessserver.requests.JwtAuthenticationResponse;
import com.diefrage.businessserver.requests.SignUpRequest;
import com.diefrage.businessserver.requests.SurveyRequest;
import com.diefrage.businessserver.services.SurveyService;
import com.diefrage.businessserver.services.UserService;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.ws.rs.core.MediaType;
import lombok.*;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Objects;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class SurveyApplicationTests {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private UserService userService;

    @Autowired
    private SurveyService surveyService;

    private String token;
    private Long userId;

    private Long newSurveyId;

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

        newSurveyId = surveyService.addNewSurvey(userId, surveyRequest).getSurveyId();
    }

    @AfterEach
    public void deleteAll() throws Exception {
        if (token == null) setup();

        surveyService.deleteSurvey(userId, newSurveyId);

        mockMvc.perform(delete("/api/professor/delete/" + userId)
                        .header("Authorization", "Bearer " + this.token)
                        .header("X-Username", "professor@professor.com")
                        .accept(org.springframework.http.MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    @Order(1)
    public void testGetAllSurveys_withUser_200() throws Exception {
        if (token == null) setup();
       MvcResult mvcResult = mockMvc.perform(get("/api/survey/{professor_id}/all", userId)
                        .header("X-Username", "professor@professor.com")
                        .header("Authorization", "Bearer " + this.token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andReturn();
    }

    @Test
    @Order(2)
    public void testGetAllSurveys_withAnotherUser() throws Exception {
        if (token == null) setup();
        mockMvc.perform(get("/api/survey/{professor_id}/all", (userId + 1))
                        .header("X-Username", "professor2@professor.com")
                        .header("Authorization", "Bearer " + this.token))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code").value("USER_NOT_FOUND"));
    }

    @Test
    @Order(3)
    public void testGetAllSurveys_byName_200() throws Exception {
        if (token == null) setup();
        mockMvc.perform(get("/api/survey/{professor_id}/by_name?survey_name=test", userId)
                        .header("X-Username", "professor@professor.com")
                        .header("Authorization", "Bearer " + this.token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)));
    }

    @Test
    @Order(4)
    public void testGetAllSurveys_byName_empty() throws Exception {
        if (token == null) setup();
        mockMvc.perform(get("/api/survey/{professor_id}/by_name?survey_name=testNO", userId)
                        .header("X-Username", "professor@professor.com")
                        .header("Authorization", "Bearer " + this.token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));
    }

    @Test
    @Order(5)
    void testAddNewSurvey() throws Exception {
        if (token == null) setup();
        String title = "Test Title";
        String description = "Test Description";
        Date dateBegin = new Date();
        String dateBeginFormatted = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSX").format(dateBegin);
        Date dateEnd = new Date(System.currentTimeMillis() + 1000);
        String dateEndFormatted = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSX").format(dateEnd);
        int maxStudents = 100;
        String status = "CREATED_STATUS";
        String questionsValue = "[{\"points\": 10, \"question\": \"What is the capital of France?\"," +
                " \"question_id\": 1, \"type_question\": \"multiple_choice\", \"time_limit_sec\": 30," +
                " \"correct_answers\": [\"Paris\"], \"incorrect_answers\": [\"London\", \"Berlin\", \"Madrid\"]}]";


        String requestBody = String.format("{\"title\":\"%s\",\"description\":\"%s\",\"date_begin\":\"%s\",\"date_end\":\"%s\",\"max_students\":%d,\"questions\": %s}",
                title, description, dateBeginFormatted, dateEndFormatted, maxStudents, questionsValue);

        mockMvc.perform(post("/api/survey/{professor_id}/add", userId)
                        .header("X-Username", "professor@professor.com")
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
                .andExpect(jsonPath("$.professor_id").value(userId))

                .andExpect(jsonPath("$.max_students", notNullValue()))
                .andExpect(jsonPath("$.max_students").value(100))

                .andExpect(jsonPath("$.code", not(emptyString())))
                .andExpect(jsonPath("$.link", not(emptyString())))
                .andExpect(jsonPath("$.qr_code", not(emptyString())))
                .andExpect(jsonPath("$.date_begin", not(emptyString())))
                .andExpect(jsonPath("$.date_end", not(emptyString())))

                .andExpect(jsonPath("$.questions", notNullValue()))

                .andExpect(jsonPath("$.status", notNullValue()))
                .andExpect(jsonPath("$.status.statusId", notNullValue()))
                .andExpect(jsonPath("$.status.name", not(emptyString())))
                .andExpect(jsonPath("$.status.name").value(status));
    }

    @Test
    @Order(6)
    void testLifeCycle() throws Exception {
        if (token == null) setup();
        mockMvc.perform(put("/api/survey/{professor_id}/{survey_id}/start", userId, newSurveyId)
                        .header("X-Username", "professor@professor.com")
                        .header("Authorization", "Bearer " + this.token)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status.name").value("STARTED_STATUS"));

        mockMvc.perform(put("/api/survey/{professor_id}/{survey_id}/stop", userId, newSurveyId)
                        .header("X-Username", "professor@professor.com")
                        .header("Authorization", "Bearer " + this.token)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status.name").value("FINISHED_STATUS"));

        mockMvc.perform(put("/api/survey/{professor_id}/{survey_id}/start", userId, newSurveyId)
                        .header("X-Username", "professor@professor.com")
                        .header("Authorization", "Bearer " + this.token)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code").value("SURVEY_NOT_FOUND"));
    }
}
