package com.example.demo.types;

public enum ErrorResponseEnum {

    BOOK_ID_NOT_FOUND("KZ-BMS-0001", "BookID %d was not found.");

    private final String code;
    private final String message;

    ErrorResponseEnum(String code, String message) {
        this.code = code;
        this.message = message;
    }

    public String getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }

    public String format(Object... args) {
        return String.format(message, args);
    }
}
