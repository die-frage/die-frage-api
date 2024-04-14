package com.diefrage.professor.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "Surveystatus")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SurveyStatus {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long statusId;

    @Column(name = "name", unique = true, nullable = false)
    private String name;
}
