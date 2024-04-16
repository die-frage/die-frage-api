package com.diefrage.answer.entities.dto;

import com.diefrage.answer.entities.Answer;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public record AnswerDTO (
        @JsonProperty("answer_id") Long answerId,
        @JsonProperty("survey_id") Long surveyId,
        @JsonProperty("student_id") Long studentId,
        @JsonProperty("answers") List<JSONAnswer> answers) {

    public static AnswerDTO fromAnswer(Answer answer) {
        return new AnswerDTO(answer.getAnswerId(),
                answer.getSurveyId(),
                answer.getStudentId(),
                answer.getAnswers());
    }
}
