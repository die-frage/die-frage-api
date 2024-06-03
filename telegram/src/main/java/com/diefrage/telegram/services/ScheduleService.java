package com.diefrage.telegram.services;

import com.diefrage.exceptions.TypicalServerException;
import com.diefrage.telegram.dto.JSONQuestion;
import com.diefrage.telegram.entities.ScheduleRecord;
import com.diefrage.telegram.dto.Survey;
import com.diefrage.telegram.repositories.ScheduleRepository;
import lombok.RequiredArgsConstructor;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ScheduleService {
    private final ScheduleRepository scheduleRepository;

    @Autowired
    private RestTemplate restTemplate;

    private final Long ScheduleRecordStatus_NOT_STARTED = 0L;
    private final Long ScheduleRecordStatus_STARTED = 1L;
    private final Long ScheduleRecordStatus_FINISHED = 2L;

    @Value("${application.telegram.token}")
    private String tokenBot;

    @Transactional
    public ScheduleRecord addScheduleRecord(Long chatId, Long surveyId) {
        if (scheduleRepository.findByChatId(chatId).isPresent())
            TypicalServerException.USER_ALREADY_EXISTS.throwException();

        Survey survey = getSurvey(surveyId);
        if (survey == null) TypicalServerException.SURVEY_NOT_FOUND.throwException();
        if (survey.getDate_end().before(new Date())) TypicalServerException.SURVEY_NOT_FOUND.throwException();

        ScheduleRecord scheduleRecord = new ScheduleRecord();
        scheduleRecord.setChatId(chatId);
        scheduleRecord.setSurveyId(surveyId);
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
    public void deleteScheduleRecordByChatId(Long chatId) {
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
            String professorServiceUrl = "http://localhost:8050";
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

    public Boolean hasAlreadyTakenSurvey(Long chatId) {
        Optional<ScheduleRecord> recordOptional = scheduleRepository.findByChatId(chatId);
        if (recordOptional.isEmpty()) return false;
        ScheduleRecord record = recordOptional.get();

        return record.getStatus().equals(ScheduleRecordStatus_FINISHED);
    }

    public void sendNextQuestionOfInteractiveSurvey(Long surveyId, Integer questionId) {
        Survey survey = getSurvey(surveyId);
        if (survey == null) TypicalServerException.SURVEY_NOT_FOUND.throwException();
        JSONQuestion question = survey.getQuestions().get(questionId);

        List<Long> chatIds = scheduleRepository.findAll()
                .stream()
                .filter(s -> s.getSurveyId().equals(surveyId))
                .map(ScheduleRecord::getChatId)
                .distinct()
                .collect(Collectors.toList());

        String message = "Ответьте на следующий опрос. Время для ответа: " + question.getTime_limit_sec() + "секунд.";
        System.out.println(chatIds.size());
        for (Long chatId : chatIds) {
            sendMessage(chatId, message);
        }

    }

    private void sendMessage(Long chatId, String message) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.set("Content-Type", "application/json;charset=utf-8");

            // keyboard
            List<List<Map<String, Object>>> inline_keyboard = new ArrayList<>();
            List<Map<String, Object>> inline_keyboard2 = new ArrayList<>();
            Map<String, Object> nextBtn = new HashMap<>();
            nextBtn.put("text", "получить вопрос");
            nextBtn.put("callback_data", "get_interactive_question");
            inline_keyboard2.add(nextBtn);
            inline_keyboard.add(inline_keyboard2);
            Map<String, Object> reply_markup = new HashMap<>();
            reply_markup.put("inline_keyboard", inline_keyboard);

            // set keyboard, message and destination
            Map<String, Object> dataMap = new HashMap<>();
            dataMap.put("chat_id", chatId);
            dataMap.put("text", message);
            dataMap.put("reply_markup", reply_markup);

            // make http request
            HttpEntity<String> entity = new HttpEntity<>(JsonUtil.objectToJson(dataMap), headers);
            ResponseEntity<String> response = restTemplate.exchange("https://api.telegram.org/bot" + tokenBot + "/sendMessage", HttpMethod.POST, entity, String.class);
            System.out.println(response);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
