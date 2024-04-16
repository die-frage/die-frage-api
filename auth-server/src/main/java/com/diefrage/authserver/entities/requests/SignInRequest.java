package com.diefrage.authserver.entities.requests;

import lombok.Data;

@Data
public class SignInRequest {
    private String email;
    private String password;
}