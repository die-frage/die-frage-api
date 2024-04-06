package com.diefrage.jwt.entity.request;

import lombok.Data;

@Data
public class SignUpRequest {
    private String email;
    private String password;
    private String firstName;
    private String lastName;
    private String patronymic;
}
