package com.diefrage.survey.entities.requests;

import com.diefrage.survey.entities.requests.JSONQuestion;
import lombok.Data;

import java.util.Date;
import java.util.List;

@Data
public class SurveyRequest {
    private String title;
    private String description;
    private Integer max_students;
    private List<JSONQuestion> questions;
    private Date date_begin;
    private Date date_end;
}
