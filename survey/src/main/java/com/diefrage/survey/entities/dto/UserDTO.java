package com.diefrage.survey.entities.dto;

public class UserDTO {
    private Long id;
    private String firstName;
    private String secondName;
    private String patronymic;
    private String email;

    public UserDTO() {
    }

    public UserDTO(Long id, String firstName, String secondName, String patronymic, String email) {
        this.id = id;
        this.firstName = firstName;
        this.secondName = secondName;
        this.patronymic = patronymic;
        this.email = email;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getSecondName() {
        return secondName;
    }

    public void setSecondName(String secondName) {
        this.secondName = secondName;
    }

    public String getPatronymic() {
        return patronymic;
    }

    public void setPatronymic(String patronymic) {
        this.patronymic = patronymic;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}

