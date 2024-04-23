package com.diefrage.telegram.entities.dto;

import java.util.Date;
import java.util.List;

public class Survey {
    private Long id;
    private String title;
    private String description;
    private Long professor_id;
    private Integer max_students;
    private String code;
    private String link;
    private String qr_code;
    private Date date_begin;
    private Date date_end;
    private List<JSONQuestion> questions;
    private SurveyStatus status;

    public Survey() {
    }

    public Survey(Long id, String title, String description, Long professor_id, Integer max_students, String code, String link, String qr_code, Date date_begin, Date date_end, List<JSONQuestion> questions, SurveyStatus status) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.professor_id = professor_id;
        this.max_students = max_students;
        this.code = code;
        this.link = link;
        this.qr_code = qr_code;
        this.date_begin = date_begin;
        this.date_end = date_end;
        this.questions = questions;
        this.status = status;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Long getProfessor_id() {
        return professor_id;
    }

    public void setProfessor_id(Long professor_id) {
        this.professor_id = professor_id;
    }

    public Integer getMax_students() {
        return max_students;
    }

    public void setMax_students(Integer max_students) {
        this.max_students = max_students;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public String getQr_code() {
        return qr_code;
    }

    public void setQr_code(String qr_code) {
        this.qr_code = qr_code;
    }

    public Date getDate_begin() {
        return date_begin;
    }

    public void setDate_begin(Date date_begin) {
        this.date_begin = date_begin;
    }

    public Date getDate_end() {
        return date_end;
    }

    public void setDate_end(Date date_end) {
        this.date_end = date_end;
    }

    public List<JSONQuestion> getQuestions() {
        return questions;
    }

    public void setQuestions(List<JSONQuestion> questions) {
        this.questions = questions;
    }

    public SurveyStatus getStatus() {
        return status;
    }

    public void setStatus(SurveyStatus status) {
        this.status = status;
    }
}

