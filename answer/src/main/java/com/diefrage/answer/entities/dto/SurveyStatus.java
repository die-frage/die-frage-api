package com.diefrage.answer.entities.dto;

public class SurveyStatus {
    private Long statusId;
    private String name;

    public SurveyStatus() {
    }

    public SurveyStatus(Long statusId, String name) {
        this.statusId = statusId;
        this.name = name;
    }

    public Long getStatusId() {
        return statusId;
    }

    public void setStatusId(Long statusId) {
        this.statusId = statusId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}

