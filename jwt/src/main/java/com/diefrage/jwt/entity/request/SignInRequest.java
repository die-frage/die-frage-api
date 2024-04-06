package com.diefrage.jwt.entity.request;

import lombok.Data;

@Data
public class SignInRequest {
    private String email;
    private String password;
}