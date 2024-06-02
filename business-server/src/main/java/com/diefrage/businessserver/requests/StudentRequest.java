package com.diefrage.businessserver.requests;

import lombok.Data;

@Data
public class StudentRequest {
    String name;
    String email;
    String group_number;
    String chat_id;
}
