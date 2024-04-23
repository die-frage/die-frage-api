package com.diefrage.businessserver.dto;

import com.diefrage.businessserver.entities.Survey;
import com.diefrage.businessserver.entities.SurveyStatus;
import com.diefrage.businessserver.requests.JSONQuestion;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Date;
import java.util.List;

public record SurveyDTO(
        @JsonProperty("id") Long id,
        @JsonProperty("title") String title,
        @JsonProperty("description") String description,
        @JsonProperty("professor_id") Long professorId,
        @JsonProperty("max_students") Integer maxStudents,
        @JsonProperty("code") String code,
        @JsonProperty("link") String link,
        @JsonProperty("qr_code") String qrCode,
        @JsonProperty("date_begin") Date dateBegin,
        @JsonProperty("date_end") Date dateEnd,
        @JsonProperty("questions") List<JSONQuestion> questions,
        @JsonProperty("status") SurveyStatus status) {

    public static SurveyDTO fromSurvey(Survey survey) {
        return new SurveyDTO(
                survey.getSurveyId(),
                survey.getTitle(),
                survey.getDescription(),
                survey.getProfessorId(),
                survey.getMaxStudents(),
                survey.getCode(),
                survey.getLink(),
                survey.getQrCode(),
                survey.getDateBegin(),
                survey.getDateEnd(),
                survey.getQuestions(),
                survey.getStatus()
        );
    }
}
