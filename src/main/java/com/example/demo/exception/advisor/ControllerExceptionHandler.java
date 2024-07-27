package com.example.demo.exception.advisor;

import com.example.demo.exception.CentralizedError;
import com.example.demo.exception.CentralizedException;
import com.example.demo.model.payload.response.CentralizedResponse;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingRequestHeaderException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;
import java.util.stream.Collectors;

@ControllerAdvice
public class ControllerExceptionHandler {
    private final Logger logger = LogManager.getLogger(this.getClass());

    @ExceptionHandler(BindException.class)
    @ResponseBody
    public ResponseEntity<?> bindExceptionHandler(BindException ex) {
        logger.catching(ex);
        List<CentralizedError> errorList = ex.getAllErrors()
                .stream()
                .map(error -> new CentralizedError(error.getCode(), error.getDefaultMessage()))
                .collect(Collectors.toList());
        var response = new CentralizedResponse<>(null, errorList);

        return ResponseEntity.badRequest().body(response);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseBody
    public ResponseEntity<?> methodArgumentNotValidExceptionHandler(MethodArgumentNotValidException ex) {
        logger.catching(ex);
        List<CentralizedError> errorList = ex.getBindingResult().getAllErrors()
                .stream()
                .map(error -> new CentralizedError(error.getCode(), error.getDefaultMessage()))
                .toList();
        var response = new CentralizedResponse<>(null, errorList);

        return ResponseEntity.badRequest().body(response);
    }

    @ExceptionHandler(CentralizedException.class)
    @ResponseBody
    public ResponseEntity<?> centralizedErrorExceptionHandler(CentralizedException ex) {
        logger.catching(ex);
        var response = new CentralizedResponse<>(null, ex.getErrors());

        return new ResponseEntity<>(response, ex.getHttpStatus());
    }

    @ExceptionHandler(MissingServletRequestParameterException.class)
    @ResponseBody
    public ResponseEntity<?> handleParamMissing(MissingServletRequestParameterException ex) {
        logger.error(ex.toString());
        CentralizedError error = new CentralizedError(HttpStatus.UNPROCESSABLE_ENTITY.toString(), ex.getMessage());
        var response = new CentralizedResponse<>(null, error);

        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(MissingRequestHeaderException.class)
    @ResponseBody
    public ResponseEntity<?> handleMissingRequestHeaderException(MissingRequestHeaderException ex) {
        logger.error(ex.toString());
        var error = new CentralizedError(HttpStatus.UNAUTHORIZED.toString(), ex.getMessage());
        var response = new CentralizedResponse<>(null, error);

        return new ResponseEntity<>(response, HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(NullPointerException.class)
    @ResponseBody
    public ResponseEntity<?> defaultExceptionHandler(Exception ex) {
        logger.catching(ex);
        List<CentralizedError> errorList = List.of(new CentralizedError(HttpStatus.INTERNAL_SERVER_ERROR.toString(), ex.getMessage()));
        var response = new CentralizedResponse<>(null, errorList);

        return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
