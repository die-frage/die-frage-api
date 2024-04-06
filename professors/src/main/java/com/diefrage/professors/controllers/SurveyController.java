package com.diefrage.professors.controllers;

import com.diefrage.professors.entities.SurveyDTO;
import com.diefrage.professors.entities.SurveyRequest;
import com.diefrage.professors.services.SurveyService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/survey")
@RequiredArgsConstructor
public class SurveyController {

    @Autowired
    private SurveyService surveyService;

    @GetMapping("/{professor_id}/all")
    public List<SurveyDTO> getAllSurveysByProfessor(@PathVariable(value = "professor_id") Long professorId) {
        return surveyService.getAllSurveysByProfessorId(professorId)
                .stream()
                .map(SurveyDTO::fromSurvey)
                .toList();
    }

    @GetMapping("/{professor_id}/by_name")
    public List<SurveyDTO> getAllSurveysByProfessorAndName(
            @PathVariable(value = "professor_id") Long professorId,
            @RequestParam(value = "survey_name") String survey_name) {
        return surveyService.getAllSurveysByProfessorIdAndName(professorId, survey_name)
                .stream()
                .map(SurveyDTO::fromSurvey)
                .toList();
    }

    @PostMapping("/{professor_id}/add")
    public SurveyDTO addSurvey(
            @PathVariable(value = "professor_id") Long professorId,
            @RequestBody SurveyRequest surveyRequest) {
        return SurveyDTO.fromSurvey(surveyService.addNewSurvey(professorId, surveyRequest));
    }

    @PostMapping("/{professor_id}/add/excel")
    public SurveyDTO addSurvey(
            @PathVariable(value = "professor_id") Long professorId,
            @RequestBody MultipartFile file) {
        SurveyRequest surveyRequest = surveyService.getRequestOfSurvey(file);
        return SurveyDTO.fromSurvey(surveyService.addNewSurvey(professorId, surveyRequest));
    }

    @PutMapping("/{professor_id}/{survey_id}/update")
    public SurveyDTO updateSurvey(
            @PathVariable(value = "professor_id") Long professorId,
            @PathVariable(value = "survey_id") Long surveyId,
            @RequestBody SurveyRequest surveyRequest) {
        return SurveyDTO.fromSurvey(surveyService.updateSurvey(professorId, surveyId, surveyRequest));
    }

    @PutMapping("/{professor_id}/{survey_id}/start")
    public SurveyDTO startSurvey(
            @PathVariable(value = "professor_id") Long professorId,
            @PathVariable(value = "survey_id") Long surveyId) {
        return SurveyDTO.fromSurvey(surveyService.startSurvey(professorId, surveyId));
    }

    @PutMapping("/{professor_id}/{survey_id}/stop")
    public SurveyDTO stopSurvey(
            @PathVariable(value = "professor_id") Long professorId,
            @PathVariable(value = "survey_id") Long surveyId) {
        return SurveyDTO.fromSurvey(surveyService.stopSurvey(professorId, surveyId));
    }

    @DeleteMapping("/{professor_id}/{survey_id}/delete")
    public SurveyDTO deleteSurvey(
            @PathVariable(value = "professor_id") Long professorId,
            @PathVariable(value = "survey_id") Long surveyId) {
        return SurveyDTO.fromSurvey(surveyService.deleteSurvey(professorId, surveyId));
    }
}
