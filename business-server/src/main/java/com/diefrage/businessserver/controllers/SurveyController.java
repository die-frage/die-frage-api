package com.diefrage.businessserver.controllers;

import com.diefrage.businessserver.dto.SurveyDTO;
import com.diefrage.businessserver.entities.User;
import com.diefrage.businessserver.requests.SurveyRequest;
import com.diefrage.businessserver.services.SurveyService;
import com.diefrage.businessserver.services.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

@RestController
@RequestMapping("/api/survey")
@RequiredArgsConstructor
public class SurveyController {

    @Autowired
    private SurveyService surveyService;

    @Autowired
    private UserService userService;

    @GetMapping("/{professor_id}/{survey_id}")
    public SurveyDTO getSurveyById(
            @PathVariable(value = "professor_id") Long professorId,
            @PathVariable(value = "survey_id") Long surveyId,
            @RequestHeader(value = "X-Username") String username) {
        if (validateUserRequest(professorId, username))
            return SurveyDTO.fromSurvey(surveyService.getSurveyById(surveyId, professorId));
        return null;
    }

    @GetMapping("/telegram/{survey_id}")
    public SurveyDTO getSurveyById(
            @PathVariable(value = "survey_id") Long surveyId) {
        return SurveyDTO.fromSurvey(surveyService.getSurveyById(surveyId));
    }

    @GetMapping("/code/{code}")
    public SurveyDTO getSurveyByCode(
            @PathVariable(value = "code") String code) {
        return SurveyDTO.fromSurvey(surveyService.getSurveyByCode(code));
    }

    @GetMapping("/{professor_id}/all")
    public List<SurveyDTO> getAllSurveysByProfessor(
            @PathVariable(value = "professor_id") Long professorId,
            @RequestHeader(value = "X-Username") String username) {
        if (validateUserRequest(professorId, username)) {
            return surveyService.getAllSurveysByProfessorId(professorId)
                    .stream()
                    .map(SurveyDTO::fromSurvey)
                    .toList();
        }
        return new LinkedList<>();
    }

    @GetMapping("/{professor_id}/by_name")
    public List<SurveyDTO> getAllSurveysByProfessorAndName(
            @PathVariable(value = "professor_id") Long professorId,
            @RequestParam(value = "survey_name") String surveyName,
            @RequestHeader(value = "X-Username") String username) {
        if (validateUserRequest(professorId, username)) {
            return surveyService.getAllSurveysByProfessorIdAndName(professorId, surveyName)
                    .stream()
                    .map(SurveyDTO::fromSurvey)
                    .toList();
        }
        return new LinkedList<>();
    }

    @PostMapping("/{professor_id}/add")
    public SurveyDTO addSurvey(
            @PathVariable(value = "professor_id") Long professorId,
            @RequestBody SurveyRequest surveyRequest,
            @RequestHeader(value = "X-Username") String username) {
        if (validateUserRequest(professorId, username))
            return SurveyDTO.fromSurvey(surveyService.addNewSurvey(professorId, surveyRequest));
        return null;
    }

    @PostMapping("/{professor_id}/add/excel")
    public SurveyDTO addSurvey(
            @PathVariable(value = "professor_id") Long professorId,
            @RequestBody MultipartFile file,
            @RequestHeader(value = "X-Username") String username) {
        if (validateUserRequest(professorId, username)) {
            SurveyRequest surveyRequest = surveyService.getRequestOfSurvey(file);
            return SurveyDTO.fromSurvey(surveyService.addNewSurvey(professorId, surveyRequest));
        }
        return null;
    }

    @PutMapping("/{professor_id}/{survey_id}/update")
    public SurveyDTO updateSurvey(
            @PathVariable(value = "professor_id") Long professorId,
            @PathVariable(value = "survey_id") Long surveyId,
            @RequestBody SurveyRequest surveyRequest,
            @RequestHeader(value = "X-Username") String username) {
        if (validateUserRequest(professorId, username)) {
            return SurveyDTO.fromSurvey(surveyService.updateSurvey(professorId, surveyId, surveyRequest));
        }
        return null;
    }

    @PutMapping("/{professor_id}/{survey_id}/start")
    public SurveyDTO startSurvey(
            @PathVariable(value = "professor_id") Long professorId,
            @PathVariable(value = "survey_id") Long surveyId,
            @RequestHeader(value = "X-Username") String username) {
        if (validateUserRequest(professorId, username)) {
            return SurveyDTO.fromSurvey(surveyService.startSurvey(professorId, surveyId));
        }
        return null;
    }

    @PutMapping("/{professor_id}/{survey_id}/stop")
    public SurveyDTO stopSurvey(
            @PathVariable(value = "professor_id") Long professorId,
            @PathVariable(value = "survey_id") Long surveyId,
            @RequestHeader(value = "X-Username") String username) {
        if (validateUserRequest(professorId, username)) {
            return SurveyDTO.fromSurvey(surveyService.stopSurvey(professorId, surveyId));
        }
        return null;
    }

    @PutMapping("/telegram/{survey_id}/start")
    public SurveyDTO startSurvey(
            @PathVariable(value = "survey_id") Long surveyId) {
        return SurveyDTO.fromSurvey(surveyService.startSurvey(surveyId));
    }

    @PutMapping("/telegram/{survey_id}/stop")
    public SurveyDTO stopSurvey(
            @PathVariable(value = "survey_id") Long surveyId) {
        return SurveyDTO.fromSurvey(surveyService.stopSurvey(surveyId));
    }

    @DeleteMapping("/{professor_id}/{survey_id}/delete")
    public SurveyDTO deleteSurvey(
            @PathVariable(value = "professor_id") Long professorId,
            @PathVariable(value = "survey_id") Long surveyId,
            @RequestHeader(value = "X-Username") String username) {
        if (validateUserRequest(professorId, username)) {
            return SurveyDTO.fromSurvey(surveyService.deleteSurvey(professorId, surveyId));
        }
        return null;
    }

    @DeleteMapping("/{professor_id}/delete/all")
    public void deleteAllSurveys(
            @PathVariable(value = "professor_id") Long professorId,
            @RequestHeader(value = "X-Username") String username) {
        if (validateUserRequest(professorId, username)) surveyService.deleteAllSurveys(professorId);
    }

    private boolean validateUserRequest(Long professorId, String username) {
        User userRequest = userService.getUserByEmail(username);
        return Objects.equals(userRequest.getId(), professorId);
    }
}
