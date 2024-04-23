package com.diefrage.businessserver.controllers;

import com.diefrage.businessserver.dto.AnswerDTO;
import com.diefrage.businessserver.services.AnswerService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/analyse")
@RequiredArgsConstructor
public class AnalyseController {

    @Autowired
    private AnswerService answerService;

    @GetMapping("/survey/{survey_id}/all")
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
}
