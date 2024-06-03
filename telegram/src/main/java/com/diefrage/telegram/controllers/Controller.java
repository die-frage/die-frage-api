package com.diefrage.telegram.controllers;

import com.diefrage.telegram.dto.ScheduleRecordDTO;
import com.diefrage.telegram.services.ScheduleService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/telegram")
@RequiredArgsConstructor
public class Controller {

    private final ScheduleService scheduleService;

    @GetMapping("/record/{chat_id}")
    public ScheduleRecordDTO getScheduler(
            @PathVariable("chat_id") Long chatId) {
        return ScheduleRecordDTO.fromScheduleRecord(scheduleService.getScheduleRecordChatId(chatId));
    }

    @GetMapping("/by_chat_id/{chat_id}")
    public Boolean hasAlreadyTakenSurvey(
            @PathVariable("chat_id") Long chatId) {
        return scheduleService.hasAlreadyTakenSurvey(chatId);
    }

    @PostMapping("/add/record")
    public ScheduleRecordDTO addRecord(
            @RequestParam("chat_id") Long chatId,
            @RequestParam("survey_id") Long surveyId) {
        return ScheduleRecordDTO.fromScheduleRecord(scheduleService.addScheduleRecord(chatId, surveyId));
    }

    @PostMapping("/start/record/{chat_id}")
    public ScheduleRecordDTO start(
            @PathVariable("chat_id") Long chatId) {
        return ScheduleRecordDTO.fromScheduleRecord(scheduleService.start(chatId));
    }

    @PostMapping("/stop/record/{chat_id}")
    public ScheduleRecordDTO stop(
            @PathVariable("chat_id") Long chatId) {
        return ScheduleRecordDTO.fromScheduleRecord(scheduleService.stop(chatId));
    }

    @PutMapping("/next/survey/{survey_id}/question/{question_id}")
    public void nextQuestionInteractive(
            @PathVariable("survey_id") Long surveyId,
            @PathVariable("question_id") Integer questionId) {

        System.out.println("[RECEIVED] LOG: S:" + surveyId + ", Q: " + questionId);
        scheduleService.sendNextQuestionOfInteractiveSurvey(surveyId, questionId);
    }

    @DeleteMapping("/record/{chat_id}")
    public void deleteScheduler(
            @PathVariable("chat_id") Long chatId) {
        scheduleService.deleteScheduleRecordByChatId(chatId);
    }

}
