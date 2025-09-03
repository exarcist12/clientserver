package ru.otus.java.error;

import java.time.LocalDateTime;

public class ErrorDto {
    private String code;
    private String message;
    private String datetime;

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getDatetime() {
        return datetime;
    }

    public void setDatetime(String datetime) {
        this.datetime = datetime;
    }

    public ErrorDto() {
    }

    public ErrorDto(String code, String message) {
        this.code = code;
        this.message = message;
        this.datetime = LocalDateTime.now().toString();
    }
}
