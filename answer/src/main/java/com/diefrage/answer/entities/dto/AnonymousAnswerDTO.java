package com.diefrage.answer.entities.dto;

import com.diefrage.answer.entities.AnonymousAnswer;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public record AnonymousAnswerDTO (
        @JsonProperty("answer_id") Long answerId,
        @JsonProperty("survey_id") Long surveyId,
        @JsonProperty("answers") List<JSONAnswer> answers) {

    public static AnonymousAnswerDTO fromAnswer(AnonymousAnswer answer) {
        return new AnonymousAnswerDTO(answer.getAnswerId(),
                answer.getSurveyId(),
                answer.getAnswers());
    }
}
