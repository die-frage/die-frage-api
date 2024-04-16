package com.diefrage.answer.controllers;

import com.diefrage.answer.entities.Answer;
import com.diefrage.answer.entities.dto.AnonymousAnswerDTO;
import com.diefrage.answer.entities.dto.AnswerDTO;
import com.diefrage.answer.entities.dto.JSONAnswer;
import com.diefrage.answer.services.AnswerService;
import com.diefrage.exceptions.TypicalServerException;
import com.diefrage.professor.entities.UserDTO;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.LinkedList;
import java.util.List;

@RestController
@RequestMapping("/api/answer")
@RequiredArgsConstructor
public class AnswerController {
    @Autowired
    private AnswerService answerService;

    @GetMapping("/{survey_id}/all")
    @Operation(summary = "Получение всех ответов на опрос профессором")
    public List<AnswerDTO> getAllAnswersBySurvey(
            @PathVariable(value = "survey_id") Long surveyId,
            @RequestParam(value = "professor_id") Long professorId,
            @RequestHeader(value = "X-Username") String username) {
        return answerService.getAnswersBySurveyId(surveyId, professorId, username)
                .stream()
                .map(AnswerDTO::fromAnswer)
                .toList();
    }

    @GetMapping("/{survey_id}/student/{student_id}")
    @Operation(summary = "Получение ответа на опрос студентом")
    public AnswerDTO getAnswerOnSurveyByStudent(
            @PathVariable(value = "survey_id") Long surveyId,
            @PathVariable(value = "student_id") Long studentId) {
        return AnswerDTO.fromAnswer(answerService.getAnswerOnSurveyByStudentId(surveyId, studentId));
    }

    @GetMapping("/{survey_id}/student/{student_id}/question/{question_id}")
    @Operation(summary = "Получение ответа на вопрос студентом")
    public JSONAnswer getAnswerOnQuestionByStudent(
            @PathVariable(value = "survey_id") Long surveyId,
            @PathVariable(value = "student_id") Long studentId,
            @PathVariable(value = "question_id") Long questionId) {
        return answerService.getAnswerOnQuestionByStudentId(surveyId, studentId, questionId);
    }

    @PostMapping("/authorised")
    public AnswerDTO addAnswerOnSurveyByStudent(
            @RequestParam(value = "survey_id") Long surveyId,
            @RequestParam(value = "student_id") Long studentId,
            @RequestParam(value = "response") String response) {
        return AnswerDTO.fromAnswer(answerService.addAnswerAuthorised(surveyId, studentId, response));
    }

    @PostMapping("/anonymous")
    public AnonymousAnswerDTO addAnswerOnSurveyByAnonymous(
            @RequestParam(value = "survey_id") Long surveyId,
            @RequestParam(value = "response") String response) {
        return AnonymousAnswerDTO.fromAnswer(answerService.addAnswerAnonymous(surveyId, response));
    }

    @PutMapping("/update/{survey_id}/student/{student_id}/question/{question_id}")
    public AnswerDTO changeAnswerOnQuestion(
            @PathVariable(value = "survey_id") Long surveyId,
            @PathVariable(value = "student_id") Long studentId,
            @PathVariable(value = "question_id") Long questionId,
            @RequestParam(value = "response") String response) {
        return AnswerDTO.fromAnswer(answerService.changeAnswerOnQuestion(surveyId, studentId, questionId, response));
    }

    @DeleteMapping("/delete/{survey_id}/student/{student_id}")
    public AnswerDTO deleteAnswer(
            @PathVariable(value = "survey_id") Long surveyId,
            @PathVariable(value = "student_id") Long studentId) {
        Answer answer = answerService.deleteAnswer(surveyId, studentId);
        return AnswerDTO.fromAnswer(answer);
    }
}
