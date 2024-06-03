package com.diefrage.telegram.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "Scheduler")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ScheduleRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long scheduleId;

    @Column(name = "survey_id", nullable = false)
    private Long surveyId;

    @Column(name = "chat_id", nullable = false, unique = true)
    private Long chatId;

    @Column(name = "status", nullable = false)
    private Long status;
}
