package com.example.demo.exception;

import org.springframework.http.HttpStatus;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class CentralizedException extends Exception {

    private final String code;
    private final HttpStatus httpStatus;
    private final List<CentralizedError> errors;

    public CentralizedException(HttpStatus httpStatus) {
        this(null, httpStatus, new ArrayList<>());
    }

    public CentralizedException(List<CentralizedError> errors, HttpStatus httpStatus) {
        this(null, httpStatus, errors);
    }

    public CentralizedException(CentralizedError errorType, HttpStatus httpStatus) {
        super(errorType.getMessage());
        this.code = errorType.getCode();
        this.httpStatus = httpStatus;
        this.errors = new ArrayList<>();
        this.errors.add(new CentralizedError(errorType.getCode(), errorType.getMessage()));
    }

    public CentralizedException(String code, HttpStatus httpStatus, List<CentralizedError> errors) {
        this.code = code;
        this.httpStatus = Objects.requireNonNull(httpStatus, "HttpStatus must not be null");
        this.errors = errors != null ? new ArrayList<>(errors) : new ArrayList<>();
    }

    public String getCode() {
        return code;
    }

    public HttpStatus getHttpStatus() {
        return httpStatus;
    }

    public List<CentralizedError> getErrors() {
        return Collections.unmodifiableList(errors);
    }

    public void addError(CentralizedError error) {
        if (error != null) {
            this.errors.add(error);
        }
    }

    @Override
    public String getMessage() {
        return String.format("httpStatus: %s, errors: %s", httpStatus, errors);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof CentralizedException)) return false;
        CentralizedException that = (CentralizedException) o;
        return Objects.equals(code, that.code) &&
                httpStatus == that.httpStatus &&
                Objects.equals(errors, that.errors);
    }

    @Override
    public int hashCode() {
        return Objects.hash(code, httpStatus, errors);
    }

    @Override
    public String toString() {
        return "CentralizedException{" +
                "code='" + code + '\'' +
                ", httpStatus=" + httpStatus +
                ", errors=" + errors +
                '}';
    }
}