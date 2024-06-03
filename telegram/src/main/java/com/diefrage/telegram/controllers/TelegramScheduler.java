package com.diefrage.telegram.controllers;

import com.diefrage.exceptions.TypicalServerException;
import com.diefrage.telegram.dto.JSONQuestion;
import com.diefrage.telegram.entities.ScheduleRecord;
import com.diefrage.telegram.dto.Survey;
import com.diefrage.telegram.dto.SurveyStatus;
import com.diefrage.telegram.repositories.ScheduleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import org.json.JSONArray;
import org.json.JSONObject;

@Component
public class TelegramScheduler {

    @Autowired
    private ScheduleRepository scheduleRepository;

    @Autowired
    RestTemplate restTemplate;

    private final Long CREATED_STATUS = 1L;
    private final Long STARTED_STATUS = 2L;
    private final Long FINISHED_STATUS = 3L;

    private final Long ScheduleRecordStatus_NOT_STARTED = 0L;
    private final Long ScheduleRecordStatus_STARTED = 1L;
    private final Long ScheduleRecordStatus_FINISHED = 2L;

    private final SurveyStatus statusFinished = new SurveyStatus(FINISHED_STATUS, "FINISHED_STATUS");

    @Value("${application.telegram.token}")
    private String tokenBot;

    @Transactional
    @Scheduled(fixedRate = 60000)
    public void checkSurveys() {
        List<Long> surveysId = scheduleRepository.findAll()
                .stream()
                .map(ScheduleRecord::getSurveyId)
                .distinct()
                .collect(Collectors.toList());
        for (Long id : surveysId) {
            Survey survey = getSurvey(id);
            if (survey == null) continue;
            if (Objects.equals(survey.getStatus().getStatusId(), CREATED_STATUS)) {
                if (survey.getDate_begin().before(new Date())) {

                    List<Long> chats = scheduleRepository.findAllBySurveyId(id)
                            .stream()
                            .filter(s -> Objects.equals(s.getStatus(), ScheduleRecordStatus_NOT_STARTED))
                            .map(ScheduleRecord::getChatId)
                            .distinct()
                            .collect(Collectors.toList());

                    for (Long chatId : chats) sendTelegramNotificationStarted(chatId);

                    startSurvey(id);
                }

            } else if (Objects.equals(survey.getStatus().getStatusId(), STARTED_STATUS)) {
                if (survey.getDate_end().before(new Date())) {
                    survey.setStatus(statusFinished);
                    List<Long> chats = scheduleRepository.findAllBySurveyId(id)
                            .stream()
                            .filter(s -> !Objects.equals(s.getStatus(), ScheduleRecordStatus_NOT_STARTED))
                            .map(ScheduleRecord::getChatId)
                            .distinct()
                            .collect(Collectors.toList());
                    for (Long chatId : chats) sendTelegramNotificationFinished(chatId);

                    stopSurvey(id);
                    scheduleRepository.deleteAllBySurveyId(id);
                }
            }
            if (Objects.equals(survey.getStatus().getStatusId(), FINISHED_STATUS)) {
                scheduleRepository.deleteAllBySurveyId(id);
            }
        }
    }

    private void sendTelegramNotificationStarted(Long chatId) {
        String message = "Опрос можно начинать проходить. ";
        String telegramBotApiUrl = "https://api.telegram.org/bot" + tokenBot + "/sendMessage?chat_id=" + chatId + "&text=" + message;
        restTemplate.postForObject(telegramBotApiUrl, null, String.class);
    }

    private void sendTelegramNotificationFinished(Long chatId) {
        String message = "Опрос автоматически завершился. ";
        String telegramBotApiUrl = "https://api.telegram.org/bot" + tokenBot + "/sendMessage?chat_id=" + chatId + "&text=" + message;
        restTemplate.postForObject(telegramBotApiUrl, null, String.class);
    }

    private Survey getSurvey(Long surveyId) {
        try {
            String professorServiceUrl = "http://localhost:8787";
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

    private void startSurvey(Long surveyId) {
        try {
            String professorServiceUrl = "http://localhost:8787";
            ResponseEntity<Survey> surveyDTOResponseEntity = restTemplate.exchange(
                    professorServiceUrl + "/api/survey/telegram/" + surveyId + "/start",
                    HttpMethod.PUT,
                    new HttpEntity<>(new HttpHeaders()),
                    Survey.class);
        } catch (HttpClientErrorException e) {
            TypicalServerException.SURVEY_NOT_FOUND.throwException();
        }
    }

    private void stopSurvey(Long surveyId) {
        try {
            String professorServiceUrl = "http://localhost:8787";
            ResponseEntity<Survey> surveyDTOResponseEntity = restTemplate.exchange(
                    professorServiceUrl + "/api/survey/telegram/" + surveyId + "/stop",
                    HttpMethod.PUT,
                    new HttpEntity<>(new HttpHeaders()),
                    Survey.class);
        } catch (HttpClientErrorException e) {
            TypicalServerException.SURVEY_NOT_FOUND.throwException();
        }
    }
}
