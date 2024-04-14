package com.diefrage.professor.entities;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.hypersistence.utils.hibernate.type.json.JsonType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.Check;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import org.hibernate.annotations.Type;

import java.util.Date;
import java.util.List;

@Entity
@Table(name = "Survey")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Survey {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long surveyId;

    @Column(name = "title", nullable = false)
    private String title;

    @ManyToOne
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = "professor_id", nullable = false)
    private User professor;

    @Column(name = "date_begin")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date dateBegin;

    @Column(name = "date_end")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date dateEnd;

    @Column(name = "max_students", nullable = false)
    @Check(constraints = "max_students > 0")
    private Integer maxStudents;

    @Column(name = "code", length = 10)
    private String code;

    @Column(name = "anonymous", nullable = false)
    private Boolean anonymous;

    @Column(name = "link")
    private String link;

    @Column(name = "qr_code")
    private String qrCode;

    @ManyToOne
    @JoinColumn(name = "status_id", nullable = false)
    private SurveyStatus status;

    @Type(JsonType.class)
    @Column(name = "questions", columnDefinition = "jsonb", nullable = false)
    private List<JSONQuestion> questions;
}
