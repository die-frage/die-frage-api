package com.diefrage.answer.services;

import com.diefrage.answer.entities.Answer;
import com.diefrage.answer.entities.dto.JSONAnswer;
import com.diefrage.answer.entities.dto.StudentDTO;
import com.diefrage.answer.entities.dto.SurveyDTO;
import com.diefrage.answer.repositories.AnswerRepository;
import com.diefrage.exceptions.TypicalServerException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AnswerService {
    private final AnswerRepository answerRepository;

    @Autowired
    private RestTemplate restTemplate;

    @Transactional
    public List<Answer> getAnswersBySurveyId(Long surveyId, Long professorId, String username) {
        SurveyDTO survey = getSurvey(surveyId, professorId, username);
        return answerRepository.findAllBySurveyId(surveyId);
    }

    @Transactional
    public Answer getAnswerOnSurveyByStudentId(Long surveyId, Long studentId) {
        StudentDTO student = getStudent(studentId);
        Optional<Answer> optionalAnswer = answerRepository.findBySurveyIdAndStudentId(surveyId, studentId);
        if (optionalAnswer.isEmpty()) {
            TypicalServerException.ANSWER_NOT_FOUND.throwException();
        }
        return optionalAnswer.get();
    }

    @Transactional
    public JSONAnswer getAnswerOnQuestionByStudentId(Long surveyId, Long studentId, Long questionId) {
        StudentDTO student = getStudent(studentId);
        Optional<Answer> optionalAnswer = answerRepository.findBySurveyIdAndStudentId(surveyId, studentId);
        if (optionalAnswer.isEmpty()) {
            TypicalServerException.ANSWER_NOT_FOUND.throwException();
        }
        Answer answer = optionalAnswer.get();
        JSONAnswer jsonAnswer = null;
        for (JSONAnswer currentAnswer : answer.getAnswers()) {
            if (Objects.equals(currentAnswer.getQuestion_id(), questionId)) {
                jsonAnswer = currentAnswer;
            }
        }
        return jsonAnswer;
    }

    @Transactional
    public Answer addAnswerAuthorised(Long surveyId, Long studentId, String response) {
        StudentDTO student = getStudent(studentId);
        Optional<Answer> optionalAnswer = answerRepository.findBySurveyIdAndStudentId(surveyId, studentId);
        Answer answer;
        JSONAnswer jsonAnswer = null;
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            System.out.println(response);
            jsonAnswer = objectMapper.readValue(response, JSONAnswer.class);
        } catch (JsonProcessingException e) {
            System.out.println(e.getMessage());
            TypicalServerException.INTERNAL_EXCEPTION.throwException();
        }

        if (optionalAnswer.isEmpty()) {
            List<JSONAnswer> jsonAnswerList = new LinkedList<>();
            jsonAnswerList.add(jsonAnswer);
            answer = new Answer();
            answer.setSurveyId(surveyId);
            answer.setStudentId(studentId);
            answer.setAnswers(jsonAnswerList);
        } else {
            answer = optionalAnswer.get();
            List<JSONAnswer> answers = answer.getAnswers();
            answers.add(jsonAnswer);
            answer.setAnswers(answers);
        }

        return answerRepository.save(answer);
    }

    public Answer changeAnswerOnQuestion(Long surveyId, Long studentId, Long questionId, String response) {
        StudentDTO student = getStudent(studentId);

        Optional<Answer> optionalAnswer = answerRepository.findBySurveyIdAndStudentId(surveyId, studentId);
        if (optionalAnswer.isEmpty()) {
            TypicalServerException.ANSWER_NOT_FOUND.throwException();
        }

        Answer answer = optionalAnswer.get();
        List<JSONAnswer> answers = answer.getAnswers();
        JSONAnswer jsonAnswer = new JSONAnswer();
        answers.removeIf(jsonA -> jsonA != null && Objects.equals(jsonA.getQuestion_id(), questionId));

        try {
            ObjectMapper objectMapper = new ObjectMapper();
            jsonAnswer = objectMapper.readValue(response, JSONAnswer.class);
        } catch (JsonProcessingException e) {
            TypicalServerException.INTERNAL_EXCEPTION.throwException();
        }

        answers.add(jsonAnswer);
        answer.setAnswers(answers);
        return answerRepository.save(answer);
    }

    public Answer deleteAnswer(Long surveyId, Long studentId) {
        StudentDTO student = getStudent(studentId);
        Optional<Answer> optionalAnswer = answerRepository.findBySurveyIdAndStudentId(surveyId, studentId);
        if (optionalAnswer.isEmpty()) {
            TypicalServerException.ANSWER_NOT_FOUND.throwException();
        }
        Answer answer = optionalAnswer.get();
        answerRepository.deleteById(answer.getAnswerId());
        return answer;
    }

    private SurveyDTO getSurvey(Long surveyId, Long professorId, String username) {
        try {
            String professorServiceUrl = "http://localhost:8040";
            ResponseEntity<SurveyDTO> surveyDTOResponseEntity = restTemplate.exchange(
                    professorServiceUrl + "/api/survey/" + professorId + "/" + surveyId,
                    HttpMethod.GET,
                    new HttpEntity<>(createHeadersWithUsername(username)),
                    SurveyDTO.class);
            if (surveyDTOResponseEntity.getStatusCode() == HttpStatus.OK)
                return surveyDTOResponseEntity.getBody();
        } catch (HttpClientErrorException e) {
            System.out.println(e);
            TypicalServerException.SURVEY_NOT_FOUND.throwException();
        }
        return null;
    }

    private StudentDTO getStudent(Long studentId) {
        try {
            String professorServiceUrl = "http://localhost:8020";
            ResponseEntity<StudentDTO> studentDTOResponseEntity = restTemplate.exchange(
                    professorServiceUrl + "/api/student/" + studentId,
                    HttpMethod.GET,
                    new HttpEntity<>(new HttpHeaders()),
                    StudentDTO.class);
            if (studentDTOResponseEntity.getStatusCode() == HttpStatus.OK)
                return studentDTOResponseEntity.getBody();
        } catch (HttpClientErrorException e) {
            System.out.println(e);
            TypicalServerException.STUDENT_NOT_FOUND.throwException();
        }
        return null;
    }

    private HttpHeaders createHeadersWithUsername(String username) {
        HttpHeaders headers = new HttpHeaders();
        headers.add("X-Username", username);
        return headers;
    }
}
