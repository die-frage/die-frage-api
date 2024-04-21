package com.diefrage.survey.services;

import com.diefrage.exceptions.TypicalServerException;
import com.diefrage.survey.entities.Survey;
import com.diefrage.survey.entities.requests.SurveyRequest;
import com.diefrage.survey.entities.SurveyStatus;
import com.diefrage.survey.repositories.StatusRepository;
import com.diefrage.survey.repositories.SurveyRepository;
import org.springframework.web.multipart.MultipartFile;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class SurveyService {
    private final StorageService storageService;
    private final ExcelService excelService;
    private final SurveyRepository surveyRepository;
    private final StatusRepository statusRepository;

    private final Long CREATED_STATUS = 1L;
    private final Long STARTED_STATUS = 2L;
    private final Long FINISHED_STATUS = 3L;

    @Transactional
    public List<Survey> getAllSurveysByProfessorId(Long professorId) {
        return surveyRepository.findAllByProfessorId(professorId);
    }

    @Transactional
    public Survey getSurveyById(Long surveyId) {
        Optional<Survey> surveyOptional = surveyRepository.findById(surveyId);
        if (surveyOptional.isEmpty()) {
            TypicalServerException.SURVEY_NOT_FOUND.throwException();
        }
        return surveyOptional.get();
    }

    @Transactional
    public Survey getSurveyById(Long surveyId, Long professorId) {
        Optional<Survey> surveyOptional = surveyRepository.findById(surveyId);
        if (surveyOptional.isEmpty()) {
            TypicalServerException.SURVEY_NOT_FOUND.throwException();
        }
        Survey survey = surveyOptional.get();
        if (!survey.getProfessorId().equals(professorId)){
            TypicalServerException.SURVEY_NOT_FOUND.throwException();
        }
        return survey;
    }

    @Transactional
    public Survey getSurveyByCode(String codeId) {
        Optional<Survey> surveyOptional = surveyRepository.findByCode(codeId);
        if (surveyOptional.isEmpty()) {
            TypicalServerException.SURVEY_NOT_FOUND.throwException();
        }
        return surveyOptional.get();
    }

    @Transactional
    public List<Survey> getAllSurveysByProfessorIdAndName(Long professorId, String surveyName) {
        return surveyRepository.findAllByProfessorId(professorId)
                .stream()
                .filter(survey -> survey.getTitle().equalsIgnoreCase(surveyName))
                .toList();
    }

    @Transactional
    public Survey addNewSurvey(Long professorId, SurveyRequest surveyRequest) {
        Optional<SurveyStatus> surveyStatus = statusRepository.findById(CREATED_STATUS);
        if (surveyStatus.isEmpty()) {
            TypicalServerException.INTERNAL_EXCEPTION.throwException();
        }
        Survey newSurvey = new Survey();

        newSurvey.setTitle(surveyRequest.getTitle());
        newSurvey.setDescription(surveyRequest.getDescription());
        newSurvey.setProfessorId(professorId);
        newSurvey.setAnonymous(surveyRequest.getAnonymous());
        newSurvey.setMaxStudents(surveyRequest.getMax_students());

        SurveyStatus status = surveyStatus.get();
        newSurvey.setStatus(status);
        newSurvey.setDateBegin(surveyRequest.getDate_begin());
        newSurvey.setDateEnd(surveyRequest.getDate_end());
        newSurvey.setQuestions(surveyRequest.getQuestions());

        String code = String.valueOf(newSurvey.hashCode());
        newSurvey.setCode(code);
        newSurvey.setLink(storageService.getTelegramLink(code));
        newSurvey.setQrCode(storageService.uploadFile(code));

        return surveyRepository.save(newSurvey);
    }

    @Transactional
    public SurveyRequest getRequestOfSurvey(MultipartFile file) {
        return excelService.parseSurvey(file);
    }

    @Transactional
    public Survey updateSurvey(Long professorId, Long surveyId, SurveyRequest surveyRequest) {
        Optional<Survey> surveyOptional = surveyRepository.findById(surveyId);
        if (surveyOptional.isEmpty()) {
            TypicalServerException.SURVEY_NOT_FOUND.throwException();
        }

        Survey survey = surveyOptional.get();
        if (!Objects.equals(survey.getProfessorId(), professorId)) {
            TypicalServerException.SURVEY_NOT_FOUND.throwException();
        }

        if (!Objects.equals(survey.getStatus().getStatusId(), CREATED_STATUS)) {
            TypicalServerException.SURVEY_NOT_FOUND.throwException();
        }

        survey.setTitle(surveyRequest.getTitle());
        survey.setDescription(surveyRequest.getDescription());
        survey.setAnonymous(surveyRequest.getAnonymous());
        survey.setMaxStudents(surveyRequest.getMax_students());
        survey.setDateBegin(surveyRequest.getDate_begin());
        survey.setDateEnd(surveyRequest.getDate_end());
        survey.setQuestions(surveyRequest.getQuestions());
        return surveyRepository.save(survey);
    }

    @Transactional
    public Survey startSurvey(Long surveyId) {
        Optional<Survey> surveyOptional = surveyRepository.findById(surveyId);
        if (surveyOptional.isEmpty()) {
            TypicalServerException.SURVEY_NOT_FOUND.throwException();
        }
        Survey survey = surveyOptional.get();

        if (!Objects.equals(survey.getStatus().getStatusId(), CREATED_STATUS)) {
            TypicalServerException.SURVEY_NOT_FOUND.throwException();
        }

        Optional<SurveyStatus> surveyStatus = statusRepository.findById(STARTED_STATUS);
        if (surveyStatus.isEmpty()) {
            TypicalServerException.INTERNAL_EXCEPTION.throwException();
        }
        SurveyStatus newStatus = surveyStatus.get();
        survey.setStatus(newStatus);
        survey.setDateBegin(new Date());
        surveyRepository.save(survey);
        return survey;
    }

    @Transactional
    public Survey startSurvey(Long professorId, Long surveyId) {
        Optional<Survey> surveyOptional = surveyRepository.findById(surveyId);
        if (surveyOptional.isEmpty()) {
            TypicalServerException.SURVEY_NOT_FOUND.throwException();
        }
        Survey survey = surveyOptional.get();
        if (!Objects.equals(survey.getProfessorId(), professorId)) {
            TypicalServerException.SURVEY_NOT_FOUND.throwException();
        }

        if (!Objects.equals(survey.getStatus().getStatusId(), CREATED_STATUS)) {
            TypicalServerException.SURVEY_NOT_FOUND.throwException();
        }

        Optional<SurveyStatus> surveyStatus = statusRepository.findById(STARTED_STATUS);
        if (surveyStatus.isEmpty()) {
            TypicalServerException.INTERNAL_EXCEPTION.throwException();
        }
        SurveyStatus newStatus = surveyStatus.get();
        survey.setStatus(newStatus);
        survey.setDateBegin(new Date());
        surveyRepository.save(survey);
        return survey;
    }

    @Transactional
    public Survey stopSurvey(Long surveyId) {
        Optional<Survey> surveyOptional = surveyRepository.findById(surveyId);
        if (surveyOptional.isEmpty()) {
            TypicalServerException.SURVEY_NOT_FOUND.throwException();
        }
        Optional<SurveyStatus> surveyStatus = statusRepository.findById(FINISHED_STATUS);
        if (surveyStatus.isEmpty()) {
            TypicalServerException.INTERNAL_EXCEPTION.throwException();
        }

        Survey survey = surveyOptional.get();
        SurveyStatus newStatus = surveyStatus.get();
        survey.setStatus(newStatus);
        survey.setDateEnd(new Date());
        surveyRepository.save(survey);
        return survey;
    }

    @Transactional
    public Survey stopSurvey(Long professorId, Long surveyId) {
        Optional<Survey> surveyOptional = surveyRepository.findById(surveyId);
        if (surveyOptional.isEmpty()) {
            TypicalServerException.SURVEY_NOT_FOUND.throwException();
        }
        Survey survey = surveyOptional.get();
        if (!Objects.equals(survey.getProfessorId(), professorId)) {
            TypicalServerException.SURVEY_NOT_FOUND.throwException();
        }
        Optional<SurveyStatus> surveyStatus = statusRepository.findById(FINISHED_STATUS);
        if (surveyStatus.isEmpty()) {
            TypicalServerException.INTERNAL_EXCEPTION.throwException();
        }
        SurveyStatus newStatus = surveyStatus.get();
        survey.setStatus(newStatus);
        survey.setDateEnd(new Date());
        surveyRepository.save(survey);
        return survey;
    }

    @Transactional
    public Survey deleteSurvey(Long professorId, Long surveyId) {
        Optional<Survey> surveyOptional = surveyRepository.findById(surveyId);
        if (surveyOptional.isEmpty()) {
            TypicalServerException.SURVEY_NOT_FOUND.throwException();
        }
        Survey survey = surveyOptional.get();
        if (!Objects.equals(survey.getProfessorId(), professorId)) {
            TypicalServerException.SURVEY_NOT_FOUND.throwException();
        }
        storageService.deleteImage(survey.getQrCode());
        surveyRepository.delete(survey);
        return survey;
    }

    @Transactional
    public void deleteAllSurveys(Long professorId) {
        List<Survey> surveys = getAllSurveysByProfessorId(professorId);
        for (Survey survey : surveys){
            storageService.deleteImage(survey.getQrCode());
            surveyRepository.delete(survey);
        }
    }
}
