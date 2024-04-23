package com.diefrage.businessserver.repositories;

import com.diefrage.businessserver.entities.SurveyStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface StatusRepository extends JpaRepository<SurveyStatus, Long> {

}
