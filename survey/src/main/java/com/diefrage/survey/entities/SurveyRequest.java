package com.diefrage.survey.entities;

import lombok.Data;

import java.util.Date;
import java.util.List;

@Data
public class SurveyRequest {
    private String title;
    private Integer max_students;
    private Boolean anonymous;
    private List<JSONQuestion> questions;
    private Date date_begin;
    private Date date_end;
}
