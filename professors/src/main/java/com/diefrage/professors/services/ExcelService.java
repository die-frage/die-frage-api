package com.diefrage.professors.services;

import com.diefrage.exceptions.TypicalServerException;
import com.diefrage.professors.entities.JSONQuestion;
import com.diefrage.professors.entities.SurveyRequest;
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

    final int NAME_ROW = 1;
    final int DESCRIPTION_ROW = 2;
    final int DATE_BEGIN_ROW = 3;
    final int TIME_BEGIN_ROW = 4;
    final int DATE_END_ROW = 5;
    final int TIME_END_ROW = 6;
    final int MAX_NUMBER_ROW = 7;
    final int ANONYMOUS_ROW = 8;
    final int COLUMN_VALUE = 2;
    final int CELL_NUMBER = 0;
    final int CELL_QUESTION = 1;
    final int CELL_TIME = 2;
    final int CELL_POINTS = 3;
    final int CELL_MULTIPLE = 4;
    final int CELL_CORRECT_ANSWER = 5;
    final int CELL_START_OF_INCORRECT_ANSWERS = 6;

    public SurveyRequest parseSurvey(MultipartFile file) {
        Workbook workbook = null;
        Sheet sheetSettings = null;
        Sheet sheetQuestions = null;
        SurveyRequest survey = new SurveyRequest();
        try (InputStream fileInputStream = file.getInputStream()) {
            workbook = new XSSFWorkbook(fileInputStream);
            sheetSettings = workbook.getSheet(GENERAL_SETTINGS_PAGE);
            sheetQuestions = workbook.getSheet(QUESTIONS_PAGE);
            if (sheetQuestions == null || sheetSettings == null)
                TypicalServerException.INVALID_EXCEL_FORMAT.throwException();
        } catch (IOException e) {
            TypicalServerException.INTERNAL_EXCEPTION.throwException();
        }

        try {
            String title = sheetSettings.getRow(NAME_ROW).getCell(COLUMN_VALUE).getStringCellValue();
            if (title == null) TypicalServerException.INVALID_EXCEL_FORMAT.throwException();
            String dateBeginString = formatDate(sheetSettings.getRow(DATE_BEGIN_ROW).getCell(COLUMN_VALUE).getDateCellValue());
            String timeBeginString = formatTime(sheetSettings.getRow(TIME_BEGIN_ROW).getCell(COLUMN_VALUE).getDateCellValue());
            String dateEndString = formatDate(sheetSettings.getRow(DATE_END_ROW).getCell(COLUMN_VALUE).getDateCellValue());
            String timeEndString = formatTime(sheetSettings.getRow(TIME_END_ROW).getCell(COLUMN_VALUE).getDateCellValue());
            int maxStudents = (int) sheetSettings.getRow(MAX_NUMBER_ROW).getCell(COLUMN_VALUE).getNumericCellValue();
            if (maxStudents <= 0) TypicalServerException.INVALID_EXCEL_FORMAT.throwException();
            String anonymousValue = sheetSettings.getRow(ANONYMOUS_ROW).getCell(COLUMN_VALUE).getStringCellValue();
            if (anonymousValue == null) TypicalServerException.INVALID_EXCEL_FORMAT.throwException();
            boolean anonymous = anonymousValue.equalsIgnoreCase("да");
            List<JSONQuestion> questions = parseQuestions(sheetQuestions);
            if (questions.size() == 0) TypicalServerException.INVALID_EXCEL_FORMAT.throwException();
            try {
                SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
                Date dateEnd = formatter.parse(dateEndString + " " + timeEndString);
                Date dateBegin = formatter.parse(dateBeginString + " " + timeBeginString);
                survey.setTitle(title);
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
            if (row.getCell(CELL_NUMBER) == null) continue;
            if (row.getCell(CELL_QUESTION) == null) continue;
            if (row.getCell(CELL_TIME) == null) continue;
            if (row.getCell(CELL_POINTS) == null) continue;
            if (row.getCell(CELL_MULTIPLE) == null) continue;
            if (row.getCell(CELL_CORRECT_ANSWER) == null) continue;
            if (!row.getCell(CELL_NUMBER).getCellType().equals(CellType.NUMERIC)) continue;

            int id = (int) row.getCell(CELL_NUMBER).getNumericCellValue();
            String question = row.getCell(CELL_QUESTION).getStringCellValue();
            int time_limit_sec = (int) row.getCell(CELL_TIME).getNumericCellValue();
            int points = (int) row.getCell(CELL_POINTS).getNumericCellValue();
            String multipleVal = row.getCell(CELL_MULTIPLE).getStringCellValue();
            boolean multiple = multipleVal.equalsIgnoreCase("да");
            String type_question = multiple ? "MULTIPLE" : "NOT_MULTIPLE";
            String correctAnswer = row.getCell(CELL_CORRECT_ANSWER).getStringCellValue();
            List<String> correct_answers = new LinkedList<>();
            correct_answers.add(correctAnswer);
            List<String> incorrect_answers = new LinkedList<>();
            String incorrectAnswer;
            int i = CELL_START_OF_INCORRECT_ANSWERS;
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
