package com.diefrage.businessserver.repositories;

import com.diefrage.businessserver.entities.Answer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AnswerRepository extends JpaRepository<Answer, Long> {
    List<Answer> findAllBySurveyId(Long surveyId);
    Optional<Answer> findBySurveyIdAndStudentId(Long surveyId, Long studentId);
}
