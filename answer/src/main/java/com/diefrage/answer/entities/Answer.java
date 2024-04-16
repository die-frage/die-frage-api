package com.diefrage.answer.entities;

import com.diefrage.answer.entities.dto.JSONAnswer;
import io.hypersistence.utils.hibernate.type.json.JsonType;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.Type;
import java.util.List;

@Entity
@Table(name = "Answer")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Answer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long answerId;

    @Column(name = "survey_id", nullable = false)
    private Long surveyId;

    @Column(name = "student_id")
    private Long studentId;

    @Type(JsonType.class)
    @Column(name = "answers", columnDefinition = "jsonb", nullable = false)
    private List<JSONAnswer> answers;
}
