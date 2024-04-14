package com.diefrage.exceptions;

import org.springframework.http.HttpStatus;

public enum TypicalServerException {
    USER_NOT_FOUND(new ServerException(HttpStatus.NOT_FOUND, "USER_NOT_FOUND", "USER_NOT_FOUND")),
    SURVEY_NOT_FOUND(new ServerException(HttpStatus.NOT_FOUND, "SURVEY_NOT_FOUND", "SURVEY_NOT_FOUND")),
    ANSWER_NOT_FOUND(new ServerException(HttpStatus.NOT_FOUND, "ANSWER_NOT_FOUND", "ANSWER_NOT_FOUND")),
    WRONG_LOGIN_PASSWORD(new ServerException(HttpStatus.BAD_REQUEST, "WRONG_LOGIN_PASSWORD", "WRONG_LOGIN_PASSWORD")),
    USER_ALREADY_EXISTS(new ServerException(HttpStatus.CONFLICT,  "USER_ALREADY_EXISTS", "USER_ALREADY_EXISTS")),
    INTERNAL_EXCEPTION(new ServerException(HttpStatus.INTERNAL_SERVER_ERROR,  "INTERNAL_SERVER_ERROR", "INTERNAL_SERVER_ERROR")),
    INVALID_DATE_BEGIN_FORMAT (new ServerException(HttpStatus.BAD_REQUEST,  "WRONG_DATE_BEGIN_FORMAT", "WRONG_DATE_BEGIN_FORMAT")),
    INVALID_DATE_END_FORMAT(new ServerException(HttpStatus.BAD_REQUEST,  "WRONG_DATE_END_FORMAT", "WRONG_DATE_END_FORMAT")),
    INVALID_EMAIL_FORMAT(new ServerException(HttpStatus.BAD_REQUEST, "INVALID_EMAIL_FORMAT", "INVALID_EMAIL_FORMAT")),
    INVALID_NAME_FORMAT(new ServerException(HttpStatus.BAD_REQUEST, "INVALID_NAME_FORMAT", "INVALID_NAME_FORMAT")),
    INVALID_PASSWORD_FORMAT(new ServerException(HttpStatus.BAD_REQUEST, "INVALID_PASSWORD_FORMAT", "INVALID_PASSWORD_FORMAT")),
    INVALID_EXCEL_FORMAT(new ServerException(HttpStatus.BAD_REQUEST, "INVALID_EXCEL_FORMAT", "INVALID_EXCEL_FORMAT"));

    private final ServerException serverException;

    TypicalServerException(ServerException serverException) {
        this.serverException = serverException;
    }

    public void throwException() {
        throw serverException;
    }
}
