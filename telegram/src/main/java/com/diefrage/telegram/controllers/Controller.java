package com.diefrage.telegram.controllers;

import com.diefrage.telegram.entities.dto.ScheduleRecordDTO;
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

    @PostMapping("/add/record")
    public ScheduleRecordDTO addRecord(
            @RequestParam("chat_id") Long chatId,
            @RequestParam("survey_id") Long surveyId,
            @RequestParam(value = "student_id", required = false) Long studentId) {
        return ScheduleRecordDTO.fromScheduleRecord(scheduleService.addScheduleRecord(chatId, surveyId, studentId));
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

    @DeleteMapping("/record/{chat_id}")
    public void deleteScheduler(
            @PathVariable("chat_id") Long chatId) {
        scheduleService.deleteScheduleRecordByChatId(chatId);
    }

}
