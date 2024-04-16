package com.diefrage.answer.entities.dto;

import lombok.*;
import java.io.Serializable;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class JSONAnswer implements Serializable {
    private Long question_id;
    private List<String> responses;
    private Integer points;
}
