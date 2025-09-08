package ru.otus.java.error;

public class BadParametersException extends ArrayIndexOutOfBoundsException{
    private String code;

    public String getCode() {
        return code;
    }

    public BadParametersException(String message, String code) {
        super(message);
        this.code = code;
    }
}
