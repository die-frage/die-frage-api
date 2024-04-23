package com.diefrage.businessserver.services;

import com.diefrage.businessserver.entities.Answer;
import com.diefrage.businessserver.entities.Student;
import com.diefrage.businessserver.entities.Survey;
import com.diefrage.businessserver.entities.User;
import com.diefrage.businessserver.requests.JSONAnswer;
import com.diefrage.businessserver.repositories.AnswerRepository;
import com.diefrage.exceptions.TypicalServerException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AnswerService {
    private final AnswerRepository answerRepository;
    private final StudentService studentService;
    private final SurveyService surveyService;

    @Transactional
    public List<Answer> getAnswersBySurveyId(User userRequest, Long surveyId, Long professorId, String username) {
        if (!Objects.equals(userRequest.getId(), professorId))
            TypicalServerException.SURVEY_NOT_FOUND.throwException();
        Survey survey = surveyService.getSurveyById(surveyId, professorId);
        return answerRepository.findAllBySurveyId(surveyId);
    }

    @Transactional
    public Answer getAnswerOnSurveyByStudentId(Long surveyId, Long studentId) {
        Student student = studentService.getStudentById(studentId);
        Optional<Answer> optionalAnswer = answerRepository.findBySurveyIdAndStudentId(surveyId, studentId);
        if (optionalAnswer.isEmpty()) {
            TypicalServerException.ANSWER_NOT_FOUND.throwException();
        }
        return optionalAnswer.get();
    }

    @Transactional
    public JSONAnswer getAnswerOnQuestionByStudentId(Long surveyId, Long studentId, Long questionId) {
        Student student = studentService.getStudentById(studentId);
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
        Student student = studentService.getStudentById(studentId);
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
        Student student = studentService.getStudentById(studentId);

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
        Student student = studentService.getStudentById(studentId);
        Optional<Answer> optionalAnswer = answerRepository.findBySurveyIdAndStudentId(surveyId, studentId);
        if (optionalAnswer.isEmpty()) {
            TypicalServerException.ANSWER_NOT_FOUND.throwException();
        }
        Answer answer = optionalAnswer.get();
        answerRepository.deleteById(answer.getAnswerId());
        return answer;
    }

}
