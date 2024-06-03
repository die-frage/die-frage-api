package com.diefrage.businessserver.requests;

import com.diefrage.businessserver.requests.JSONQuestion;
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
    private Boolean is_interactive;
}
