package com.example.demo.exception;

import com.example.demo.types.ErrorResponseEnum;
import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.Objects;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class CentralizedError {
    private String code;
    private String message;

    public CentralizedError() {
    }

    public CentralizedError(String code, String message) {
        this.code = code;
        this.message = message;
    }

    public CentralizedError(ErrorResponseEnum errorType) {
        this.code = errorType.getCode();
        this.message = errorType.getMessage();
    }

    public CentralizedError(ErrorResponseEnum errorType, Object... str) {
        this.code = errorType.getCode();
        this.message = String.format(errorType.getMessage(), str);
    }

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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CentralizedError that = (CentralizedError) o;
        return Objects.equals(code, that.code) && Objects.equals(message, that.message);
    }

    @Override
    public int hashCode() {
        return Objects.hash(code, message);
    }

    @Override
    public String toString() {
        return "CentralizedError{" +
                "code='" + code + '\'' +
                ", message='" + message + '\'' +
                '}';
    }
}