package com.diefrage.answer.repositories;

import com.diefrage.answer.entities.AnonymousAnswer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AnonymousAnswerRepository  extends JpaRepository<AnonymousAnswer, Long> {
    Optional<AnonymousAnswer> findBySurveyId(Long surveyId);
}
