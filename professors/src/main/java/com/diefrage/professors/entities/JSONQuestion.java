package com.diefrage.professors.entities;

import lombok.*;

import java.io.Serializable;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class JSONQuestion implements Serializable {
    private Integer question_id;
    private String question;
    private String type_question;
    private List<String> incorrect_answers;
    private List<String> correct_answers;
    private Integer points;
    private Integer time_limit_sec;
}
