package com.diefrage.survey.repositories;

import com.diefrage.professor.entities.Survey;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SurveyRepository extends JpaRepository<Survey, Long> {
    List<Survey> findAllByProfessorId(Long id);
    Optional<Survey> findByCode(String code);
}
