package com.diefrage.telegram.dto;

import com.diefrage.telegram.entities.ScheduleRecord;
import com.fasterxml.jackson.annotation.JsonProperty;

public record ScheduleRecordDTO(
        @JsonProperty("schedule_id") Long schedule_id,
        @JsonProperty("chat_id") Long chat_id,
        @JsonProperty("status") Long status,
        @JsonProperty("survey_id") Long survey_id){

    public static ScheduleRecordDTO fromScheduleRecord(ScheduleRecord scheduleRecord){
        return new ScheduleRecordDTO(
                scheduleRecord.getScheduleId(),
                scheduleRecord.getChatId(),
                scheduleRecord.getStatus(),
                scheduleRecord.getSurveyId()
        );
    }
}
