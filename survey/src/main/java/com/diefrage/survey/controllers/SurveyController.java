package com.diefrage.survey.controllers;

import com.diefrage.exceptions.TypicalServerException;
import com.diefrage.professor.entities.UserDTO;
import com.diefrage.survey.entities.SurveyDTO;
import com.diefrage.survey.entities.SurveyRequest;
import com.diefrage.survey.services.SurveyService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import java.util.LinkedList;
import java.util.List;

@RestController
@RequestMapping("/api/survey")
@RequiredArgsConstructor
public class SurveyController {

    @Autowired
    private SurveyService surveyService;

    @Autowired
    private RestTemplate restTemplate;

    private final String professorServiceUrl = "http://localhost:8030";

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
            @RequestParam(value = "survey_name") String survey_name,
            @RequestHeader(value = "X-Username") String username) {

        if (validateUserRequest(professorId, username)) {
            return surveyService.getAllSurveysByProfessorIdAndName(professorId, survey_name)
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
        if (validateUserRequest(professorId, username)) {
            return SurveyDTO.fromSurvey(surveyService.addNewSurvey(professorId, surveyRequest));
        }
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

    private HttpHeaders createHeadersWithUsername(String username) {
        HttpHeaders headers = new HttpHeaders();
        headers.add("X-Username", username);
        return headers;
    }

    private boolean validateUserRequest(Long professorId, String username) {
        try {
            ResponseEntity<UserDTO> professorResponse = restTemplate.exchange(
                    professorServiceUrl + "/api/professor/" + professorId,
                    HttpMethod.GET,
                    new HttpEntity<>(createHeadersWithUsername(username)),
                    UserDTO.class);
            if (professorResponse.getStatusCode() == HttpStatus.OK)
                return true;
        } catch (HttpClientErrorException e) {
            TypicalServerException.USER_NOT_FOUND.throwException();
        }
        return false;
    }
}
