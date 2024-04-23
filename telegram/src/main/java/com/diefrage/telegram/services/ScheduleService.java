package com.diefrage.telegram.services;

import com.diefrage.exceptions.TypicalServerException;
import com.diefrage.telegram.entities.ScheduleRecord;
import com.diefrage.telegram.entities.dto.Survey;
import com.diefrage.telegram.repositories.ScheduleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.Date;
import java.util.Objects;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ScheduleService {
    private final ScheduleRepository scheduleRepository;

    @Autowired
    private RestTemplate restTemplate;

    private final Long ScheduleRecordStatus_NOT_STARTED = 0L;
    private final Long ScheduleRecordStatus_STARTED = 1L;
    private final Long ScheduleRecordStatus_FINISHED = 2L;

    @Transactional
    public ScheduleRecord addScheduleRecord(Long chatId, Long surveyId, Long studentId) {
        if (scheduleRepository.findByChatId(chatId).isPresent())
            TypicalServerException.USER_ALREADY_EXISTS.throwException();

        Survey survey = getSurvey(surveyId);
        if (survey == null) TypicalServerException.SURVEY_NOT_FOUND.throwException();
        if (survey.getDate_end().before(new Date())) TypicalServerException.SURVEY_NOT_FOUND.throwException();

        ScheduleRecord scheduleRecord = new ScheduleRecord();
        scheduleRecord.setChatId(chatId);
        scheduleRecord.setSurveyId(surveyId);
        scheduleRecord.setStudentId(studentId);
        scheduleRecord.setStatus(ScheduleRecordStatus_NOT_STARTED);
        return scheduleRepository.save(scheduleRecord);
    }

    @Transactional
    public ScheduleRecord getScheduleRecordChatId(Long chatId) {
        Optional<ScheduleRecord> optionalScheduleRecord = scheduleRepository.findByChatId(chatId);
        if (optionalScheduleRecord.isEmpty())
            return null;
        return optionalScheduleRecord.get();
    }

    @Transactional
    public void deleteScheduleRecordByChatId(Long chatId){
        if (scheduleRepository.findByChatId(chatId).isEmpty())
            TypicalServerException.USER_NOT_FOUND.throwException();

        scheduleRepository.deleteAllByChatId(chatId);
    }

    @Transactional
    public ScheduleRecord start(Long chatId) {
        Optional<ScheduleRecord> scheduleRecordOptional = scheduleRepository.findByChatId(chatId);
        if (scheduleRecordOptional.isEmpty())
            TypicalServerException.USER_NOT_FOUND.throwException();

        ScheduleRecord scheduleRecord = scheduleRecordOptional.get();
        if (!Objects.equals(scheduleRecord.getStatus(), ScheduleRecordStatus_NOT_STARTED))
            TypicalServerException.USER_ALREADY_EXISTS.throwException();

        scheduleRecord.setStatus(ScheduleRecordStatus_STARTED);
        return scheduleRepository.save(scheduleRecord);
    }

    @Transactional
    public ScheduleRecord stop(Long chatId) {
        Optional<ScheduleRecord> scheduleRecordOptional = scheduleRepository.findByChatId(chatId);
        if (scheduleRecordOptional.isEmpty())
            TypicalServerException.USER_NOT_FOUND.throwException();

        ScheduleRecord scheduleRecord = scheduleRecordOptional.get();
        if (!Objects.equals(scheduleRecord.getStatus(), ScheduleRecordStatus_STARTED))
            TypicalServerException.USER_ALREADY_EXISTS.throwException();

        scheduleRecord.setStatus(ScheduleRecordStatus_FINISHED);
        return scheduleRepository.save(scheduleRecord);
    }

    private Survey getSurvey(Long surveyId) {
        try {
            String professorServiceUrl = "http://localhost:8040";
            ResponseEntity<Survey> surveyDTOResponseEntity = restTemplate.exchange(
                    professorServiceUrl + "/api/survey/telegram/" + surveyId,
                    HttpMethod.GET,
                    new HttpEntity<>(new HttpHeaders()),
                    Survey.class);
            if (surveyDTOResponseEntity.getStatusCode() == HttpStatus.OK)
                return surveyDTOResponseEntity.getBody();
        } catch (HttpClientErrorException e) {
            TypicalServerException.SURVEY_NOT_FOUND.throwException();
        }
        return null;
    }
}
