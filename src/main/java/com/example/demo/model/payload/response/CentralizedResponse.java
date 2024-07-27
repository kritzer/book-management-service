package com.example.demo.model.payload.response;

import com.example.demo.exception.CentralizedError;
import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class CentralizedResponse<T> {
    private final List<T> data;
    private final List<CentralizedError> errors;

    public CentralizedResponse() {
        this.data = new ArrayList<>();
        this.errors = new ArrayList<>();
    }

    public CentralizedResponse(List<T> data, List<CentralizedError> errors) {
        this.data = data != null ? new ArrayList<>(data) : new ArrayList<>();
        this.errors = errors != null ? new ArrayList<>(errors) : new ArrayList<>();
    }

    public CentralizedResponse(T input) {
        this.data = input != null ? Collections.singletonList(input) : new ArrayList<>();
        this.errors = new ArrayList<>();
    }

    public CentralizedResponse(List<T> inputList) {
        this.data = inputList != null ? new ArrayList<>(inputList) : new ArrayList<>();
        this.errors = new ArrayList<>();
    }

    public CentralizedResponse(T input, CentralizedError error) {
        this.data = input != null ? Collections.singletonList(input) : new ArrayList<>();
        this.errors = error != null ? Collections.singletonList(error) : new ArrayList<>();
    }

    public CentralizedResponse(T input, List<CentralizedError> errorList) {
        this.data = input != null ? Collections.singletonList(input) : new ArrayList<>();
        this.errors = errorList != null ? new ArrayList<>(errorList) : new ArrayList<>();
    }

    public List<T> getData() {
        return Collections.unmodifiableList(data);
    }

    public List<CentralizedError> getErrors() {
        return Collections.unmodifiableList(errors);
    }

    public void addData(T item) {
        if (item != null) {
            this.data.add(item);
        }
    }

    public void addError(CentralizedError error) {
        if (error != null) {
            this.errors.add(error);
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CentralizedResponse<?> that = (CentralizedResponse<?>) o;
        return Objects.equals(data, that.data) && Objects.equals(errors, that.errors);
    }

    @Override
    public int hashCode() {
        return Objects.hash(data, errors);
    }

    @Override
    public String toString() {
        return "CentralizedResponse{" +
                "data=" + data +
                ", errors=" + errors +
                '}';
    }
}
