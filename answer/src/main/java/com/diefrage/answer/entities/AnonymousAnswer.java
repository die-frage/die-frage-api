package com.diefrage.answer.entities;

import com.diefrage.answer.entities.dto.JSONAnswer;
import io.hypersistence.utils.hibernate.type.json.JsonType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.Type;

import java.util.List;

@Entity
@Table(name = "Anonymousanswer")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AnonymousAnswer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long answerId;

    @Column(name = "survey_id", nullable = false)
    private Long surveyId;

    @Type(JsonType.class)
    @Column(name = "answers", columnDefinition = "jsonb", nullable = false)
    private List<JSONAnswer> answers;
}
