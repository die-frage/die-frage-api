package com.diefrage.answer.entities.dto;

import java.util.Date;
import java.util.List;

public class SurveyDTO {
    private Long id;
    private String title;
    private String description;
    private Long professorId;
    private Integer maxStudents;
    private String code;
    private String link;
    private String qrCode;
    private Date dateBegin;
    private Date dateEnd;
    private List<JSONQuestion> questions;
    private SurveyStatus status;

    public SurveyDTO() {
    }

    public SurveyDTO(Long id, String title, String description, Long professorId, Integer maxStudents, String code,
                     String link, String qrCode, Date dateBegin, Date dateEnd,
                     List<JSONQuestion> questions, SurveyStatus status) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.professorId = professorId;
        this.maxStudents = maxStudents;
        this.code = code;
        this.link = link;
        this.qrCode = qrCode;
        this.dateBegin = dateBegin;
        this.dateEnd = dateEnd;
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

    public Long getProfessorId() {
        return professorId;
    }

    public void setProfessorId(Long professorId) {
        this.professorId = professorId;
    }

    public Integer getMaxStudents() {
        return maxStudents;
    }

    public void setMaxStudents(Integer maxStudents) {
        this.maxStudents = maxStudents;
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

    public String getQrCode() {
        return qrCode;
    }

    public void setQrCode(String qrCode) {
        this.qrCode = qrCode;
    }

    public Date getDateBegin() {
        return dateBegin;
    }

    public void setDateBegin(Date dateBegin) {
        this.dateBegin = dateBegin;
    }

    public Date getDateEnd() {
        return dateEnd;
    }

    public void setDateEnd(Date dateEnd) {
        this.dateEnd = dateEnd;
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

