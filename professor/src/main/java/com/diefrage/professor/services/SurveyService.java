package com.diefrage.professor.services;

import com.diefrage.exceptions.TypicalServerException;
import com.diefrage.professor.entities.Survey;
import com.diefrage.professor.entities.SurveyRequest;
import com.diefrage.professor.entities.SurveyStatus;
import com.diefrage.professor.repositories.StatusRepository;
import com.diefrage.professor.repositories.SurveyRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class SurveyService {
    private final StorageService storageService;
//    private final UserService userService;
    private final ExcelService excelService;

    private final SurveyRepository surveyRepository;
    private final StatusRepository statusRepository;

    private final Long CREATED_STATUS_ID = 1L;
    private final Long STARTED_STATUS_ID = 2L;
    private final Long FINISHED_STATUS_ID = 3L;

    @Transactional
    public List<Survey> getAllSurveysByProfessorId(Long professorId) {
//        User user = userService.getCurrentUser();
//        if (!user.getId().equals(professorId) || !userRepository.existsById(professorId)) {
//            TypicalServerException.USER_NOT_FOUND.throwException();
//        }
        return surveyRepository.findAllByProfessorId(professorId);
    }

    @Transactional
    public List<Survey> getAllSurveysByProfessorIdAndName(Long professorId, String survey_name) {
//        User user = userService.getCurrentUser();
//        if (!user.getId().equals(professorId) || !userRepository.existsById(professorId)) {
//            TypicalServerException.USER_NOT_FOUND.throwException();
//        }
        return surveyRepository.findAllByProfessorId(professorId)
                .stream()
                .filter(survey -> survey.getTitle().equalsIgnoreCase(survey_name))
                .toList();
    }

    @Transactional
    public Survey addNewSurvey(Long professorId, SurveyRequest surveyRequest) {
//        User user = userService.getCurrentUser();
//        if (!user.getId().equals(professorId) || !userRepository.existsById(professorId)) {
//            TypicalServerException.USER_NOT_FOUND.throwException();
//        }
        Optional<SurveyStatus> surveyStatus = statusRepository.findById(CREATED_STATUS_ID);
        if (surveyStatus.isEmpty()) {
            TypicalServerException.INTERNAL_EXCEPTION.throwException();
        }
        SurveyStatus status = surveyStatus.get();

        Survey newSurvey = new Survey();
        String code = String.valueOf(newSurvey.hashCode());

//        newSurvey.setProfessor(user);
        newSurvey.setTitle(surveyRequest.getTitle());
        newSurvey.setAnonymous(surveyRequest.getAnonymous());
        newSurvey.setMaxStudents(surveyRequest.getMax_students());
        newSurvey.setStatus(status);
        newSurvey.setDateBegin(surveyRequest.getDate_begin());
        newSurvey.setDateEnd(surveyRequest.getDate_end());
        newSurvey.setQuestions(surveyRequest.getQuestions());

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
//        User user = userService.getCurrentUser();
//        if (!user.getId().equals(professorId) || !userRepository.existsById(professorId)) {
//            TypicalServerException.USER_NOT_FOUND.throwException();
//        }
        Optional<Survey> surveyOptional = surveyRepository.findById(surveyId);
        if (surveyOptional.isEmpty()) {
            TypicalServerException.SURVEY_NOT_FOUND.throwException();
        }
        Survey survey = surveyOptional.get();
        if (!Objects.equals(survey.getProfessor().getId(), professorId)) {
            TypicalServerException.SURVEY_NOT_FOUND.throwException();
        }

        if (!Objects.equals(survey.getStatus().getStatusId(), CREATED_STATUS_ID)) {
            TypicalServerException.SURVEY_NOT_FOUND.throwException();
        }

        survey.setTitle(surveyRequest.getTitle());
        survey.setAnonymous(surveyRequest.getAnonymous());
        survey.setMaxStudents(surveyRequest.getMax_students());
        survey.setDateBegin(surveyRequest.getDate_begin());
        survey.setDateEnd(surveyRequest.getDate_end());
        survey.setQuestions(surveyRequest.getQuestions());
        return surveyRepository.save(survey);
    }

    @Transactional
    public Survey startSurvey(Long professorId, Long surveyId) {
//        User user = userService.getCurrentUser();
//        if (!user.getId().equals(professorId) || !userRepository.existsById(professorId)) {
//            TypicalServerException.USER_NOT_FOUND.throwException();
//        }
        Optional<Survey> surveyOptional = surveyRepository.findById(surveyId);
        if (surveyOptional.isEmpty()) {
            TypicalServerException.SURVEY_NOT_FOUND.throwException();
        }
        Survey survey = surveyOptional.get();
        if (!Objects.equals(survey.getProfessor().getId(), professorId)) {
            TypicalServerException.SURVEY_NOT_FOUND.throwException();
        }

        if (!Objects.equals(survey.getStatus().getStatusId(), CREATED_STATUS_ID)) {
            TypicalServerException.SURVEY_NOT_FOUND.throwException();
        }

        Optional<SurveyStatus> surveyStatus = statusRepository.findById(STARTED_STATUS_ID);
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
    public Survey stopSurvey(Long professorId, Long surveyId) {
//        User user = userService.getCurrentUser();
//        if (!user.getId().equals(professorId) || !userRepository.existsById(professorId)) {
//            TypicalServerException.USER_NOT_FOUND.throwException();
//        }
        Optional<Survey> surveyOptional = surveyRepository.findById(surveyId);
        if (surveyOptional.isEmpty()) {
            TypicalServerException.SURVEY_NOT_FOUND.throwException();
        }
        Survey survey = surveyOptional.get();
        if (!Objects.equals(survey.getProfessor().getId(), professorId)) {
            TypicalServerException.SURVEY_NOT_FOUND.throwException();
        }
        if (!Objects.equals(survey.getStatus().getStatusId(), STARTED_STATUS_ID)) {
            TypicalServerException.SURVEY_NOT_FOUND.throwException();
        }

        Optional<SurveyStatus> surveyStatus = statusRepository.findById(FINISHED_STATUS_ID);
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
//        User user = userService.getCurrentUser();
//        if (!user.getId().equals(professorId) || !userRepository.existsById(professorId)) {
//            TypicalServerException.USER_NOT_FOUND.throwException();
//        }
        Optional<Survey> surveyOptional = surveyRepository.findById(surveyId);
        if (surveyOptional.isEmpty()) {
            TypicalServerException.SURVEY_NOT_FOUND.throwException();
        }
        Survey survey = surveyOptional.get();
        if (!Objects.equals(survey.getProfessor().getId(), professorId)) {
            TypicalServerException.SURVEY_NOT_FOUND.throwException();
        }
        storageService.deleteImage(survey.getQrCode());
        surveyRepository.delete(survey);
        return survey;
    }
}
