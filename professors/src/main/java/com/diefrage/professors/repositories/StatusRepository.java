package com.diefrage.professors.repositories;

import com.diefrage.professors.entities.SurveyStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface StatusRepository extends JpaRepository<SurveyStatus, Long> {

}
