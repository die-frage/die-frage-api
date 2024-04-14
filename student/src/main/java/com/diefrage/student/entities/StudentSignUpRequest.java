package com.diefrage.student.entities;

import lombok.Data;

@Data
public class StudentSignUpRequest {
    String name;
    String email;
    String group_number;
}
