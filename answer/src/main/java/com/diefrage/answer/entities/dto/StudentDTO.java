package com.diefrage.answer.entities.dto;

public class StudentDTO {
    private Long id;
    private String name;
    private String groupNumber;
    private String email;

    public StudentDTO() {
    }

    public StudentDTO(Long id, String name, String groupNumber, String email) {
        this.id = id;
        this.name = name;
        this.groupNumber = groupNumber;
        this.email = email;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getGroupNumber() {
        return groupNumber;
    }

    public void setGroupNumber(String groupNumber) {
        this.groupNumber = groupNumber;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}

