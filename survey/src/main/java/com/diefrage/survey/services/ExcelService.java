package com.diefrage.survey.services;

import com.diefrage.exceptions.TypicalServerException;
import com.diefrage.survey.entities.requests.JSONQuestion;
import com.diefrage.survey.entities.requests.SurveyRequest;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

@Service
public class ExcelService {
    private final static String GENERAL_SETTINGS_PAGE = "1. Опрос";
    private final static String QUESTIONS_PAGE = "2. Вопросы";

    final int ROW_NAME = 1;
    final int ROW_DESCRIPTION = 2;
    final int ROW_DATE_BEGIN = 3;
    final int ROW_TIME_BEGIN = 4;
    final int ROW_DATE_END = 5;
    final int ROW_TIME_END = 6;
    final int ROW_MAX_NUMBER = 7;
    final int ROW_ANONYMOUS = 8;
    final int COLUMN_VALUE = 2;

    final int COLUMN_NUMBER = 0;
    final int COLUMN_QUESTION = 1;
    final int COLUMN_TIME = 2;
    final int COLUMN_POINTS = 3;
    final int COLUMN_MULTIPLE = 4;
    final int COLUMN_CORRECT_ANSWER = 5;
    final int COLUMN_START_OF_INCORRECT_ANSWERS = 6;

    public SurveyRequest parseSurvey(MultipartFile file) {
        Workbook workbook = null;
        Sheet sheetSettings = null;
        Sheet sheetQuestions = null;
        try (InputStream fileInputStream = file.getInputStream()) {
            workbook = new XSSFWorkbook(fileInputStream);
            sheetSettings = workbook.getSheet(GENERAL_SETTINGS_PAGE);
            sheetQuestions = workbook.getSheet(QUESTIONS_PAGE);
            if (sheetQuestions == null || sheetSettings == null)
                TypicalServerException.INVALID_EXCEL_FORMAT.throwException();
        } catch (IOException e) {
            TypicalServerException.INTERNAL_EXCEPTION.throwException();
        }

        SurveyRequest survey = new SurveyRequest();

        try {
            String title = sheetSettings.getRow(ROW_NAME).getCell(COLUMN_VALUE).getStringCellValue();
            if (title == null) TypicalServerException.INVALID_EXCEL_FORMAT.throwException();

            String description = sheetSettings.getRow(ROW_DESCRIPTION).getCell(COLUMN_VALUE).getStringCellValue();
            if (description == null) TypicalServerException.INVALID_EXCEL_FORMAT.throwException();

            String dateBeginString = formatDate(sheetSettings.getRow(ROW_DATE_BEGIN).getCell(COLUMN_VALUE).getDateCellValue());

            String timeBeginString = formatTime(sheetSettings.getRow(ROW_TIME_BEGIN).getCell(COLUMN_VALUE).getDateCellValue());

            String dateEndString = formatDate(sheetSettings.getRow(ROW_DATE_END).getCell(COLUMN_VALUE).getDateCellValue());

            String timeEndString = formatTime(sheetSettings.getRow(ROW_TIME_END).getCell(COLUMN_VALUE).getDateCellValue());

            int maxStudents = (int) sheetSettings.getRow(ROW_MAX_NUMBER).getCell(COLUMN_VALUE).getNumericCellValue();
            if (maxStudents <= 0) TypicalServerException.INVALID_EXCEL_FORMAT.throwException();

            String anonymousValue = sheetSettings.getRow(ROW_ANONYMOUS).getCell(COLUMN_VALUE).getStringCellValue();
            if (anonymousValue == null) TypicalServerException.INVALID_EXCEL_FORMAT.throwException();
            boolean anonymous = anonymousValue.equalsIgnoreCase("да");

            List<JSONQuestion> questions = parseQuestions(sheetQuestions);
            if (questions.size() == 0) TypicalServerException.INVALID_EXCEL_FORMAT.throwException();
            try {
                SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
                Date dateEnd = formatter.parse(dateEndString + " " + timeEndString);
                Date dateBegin = formatter.parse(dateBeginString + " " + timeBeginString);
                survey.setTitle(title);
                survey.setDescription(description);
                survey.setMax_students(maxStudents);
                survey.setAnonymous(anonymous);
                survey.setQuestions(questions);
                survey.setDate_begin(dateBegin);
                survey.setDate_end(dateEnd);
            } catch (ParseException e) {
                TypicalServerException.INVALID_EXCEL_FORMAT.throwException();
            }
        } catch (IllegalStateException e) {
            TypicalServerException.INVALID_EXCEL_FORMAT.throwException();
        }
        try {
            workbook.close();
        } catch (IOException e) {
            TypicalServerException.INTERNAL_EXCEPTION.throwException();
        }
        return survey;
    }

    public List<JSONQuestion> parseQuestions(Sheet sheetQuestions) {
        List<JSONQuestion> questions = new LinkedList<>();
        for (Row row : sheetQuestions) {
            if (row.getCell(COLUMN_NUMBER) == null) continue;
            if (row.getCell(COLUMN_QUESTION) == null) continue;
            if (row.getCell(COLUMN_TIME) == null) continue;
            if (row.getCell(COLUMN_POINTS) == null) continue;
            if (row.getCell(COLUMN_MULTIPLE) == null) continue;
            if (row.getCell(COLUMN_CORRECT_ANSWER) == null) continue;
            if (!row.getCell(COLUMN_NUMBER).getCellType().equals(CellType.NUMERIC)) continue;

            int id = (int) row.getCell(COLUMN_NUMBER).getNumericCellValue();
            String question = row.getCell(COLUMN_QUESTION).getStringCellValue();
            int time_limit_sec = (int) row.getCell(COLUMN_TIME).getNumericCellValue();
            int points = (int) row.getCell(COLUMN_POINTS).getNumericCellValue();
            String multipleVal = row.getCell(COLUMN_MULTIPLE).getStringCellValue();
            boolean multiple = multipleVal.equalsIgnoreCase("да");
            String type_question = multiple ? "MULTIPLE" : "NOT_MULTIPLE";
            String correctAnswer = row.getCell(COLUMN_CORRECT_ANSWER).getStringCellValue();
            List<String> correct_answers = new LinkedList<>();
            correct_answers.add(correctAnswer);
            List<String> incorrect_answers = new LinkedList<>();
            String incorrectAnswer;
            int i = COLUMN_START_OF_INCORRECT_ANSWERS;
            do {
                if (row.getCell(i) == null) break;
                incorrectAnswer = row.getCell(i).getStringCellValue();
                if (incorrectAnswer != null)
                    incorrect_answers.add(incorrectAnswer);
                i++;
            }
            while (incorrectAnswer != null);
            questions.add(new JSONQuestion(id, question, type_question, incorrect_answers, correct_answers, points, time_limit_sec));
        }
        return questions;
    }

    private static String formatDate(Date date) {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        return formatter.format(date);
    }

    private static String formatTime(Date time) {
        SimpleDateFormat formatter = new SimpleDateFormat("hh:mm:ss");
        return formatter.format(time);
    }
}
