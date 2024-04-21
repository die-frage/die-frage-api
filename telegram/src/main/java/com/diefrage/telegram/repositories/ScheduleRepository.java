package com.diefrage.telegram.repositories;

import com.diefrage.telegram.entities.ScheduleRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ScheduleRepository extends JpaRepository<ScheduleRecord, Long> {
    @Modifying
    @Query("delete from ScheduleRecord s where s.surveyId = :surveyId")
    void deleteAllBySurveyId(@Param("surveyId") Long surveyId);

    List<ScheduleRecord> findAllBySurveyId(Long surveyId);

    Optional<ScheduleRecord> findByChatId(Long chatId);

    void deleteAllByChatId(Long chatId);
}
