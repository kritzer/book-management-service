package com.example.demo.exception;

import com.example.demo.exception.advisor.ControllerExceptionHandler;
import com.example.demo.model.payload.response.CentralizedResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingRequestHeaderException;
import org.springframework.web.bind.MissingServletRequestParameterException;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;


@ExtendWith(MockitoExtension.class)
class ControllerExceptionHandlerTest {

    private ControllerExceptionHandler exceptionHandler;

    @Mock
    private BindException bindException;

    @Mock
    private MissingRequestHeaderException missingRequestHeaderException;

    @BeforeEach
    void setUp() {
        exceptionHandler = new ControllerExceptionHandler();
    }

    @Test
    void bindExceptionHandler_ShouldReturnBadRequest() {
        FieldError fieldError = new FieldError("objectName", "field", "defaultMessage");
        List<ObjectError> errors = List.of(fieldError);
        when(bindException.getAllErrors()).thenReturn(errors);

        ResponseEntity<?> response = exceptionHandler.bindExceptionHandler(bindException);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertInstanceOf(CentralizedResponse.class, response.getBody());
        CentralizedResponse<?> centralizedResponse = (CentralizedResponse<?>) response.getBody();
        assertNotNull(centralizedResponse);
        assertEquals(1, centralizedResponse.getErrors().size());
        CentralizedError error = centralizedResponse.getErrors().get(0);
        assertNull(error.getCode());
        assertEquals("defaultMessage", error.getMessage());
    }

    @Test
    void methodArgumentNotValidExceptionHandler_ShouldReturnBadRequest() {
        MethodArgumentNotValidException ex = mock(MethodArgumentNotValidException.class);
        BindingResult bindingResult = mock(BindingResult.class);
        FieldError fieldError = new FieldError("objectName", "field", "defaultMessage");

        when(ex.getBindingResult()).thenReturn(bindingResult);
        when(bindingResult.getAllErrors()).thenReturn(List.of(fieldError));

        ResponseEntity<?> response = exceptionHandler.methodArgumentNotValidExceptionHandler(ex);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertInstanceOf(CentralizedResponse.class, response.getBody());
        CentralizedResponse<?> centralizedResponse = (CentralizedResponse<?>) response.getBody();
        assertNotNull(centralizedResponse);
        assertEquals(1, centralizedResponse.getErrors().size());
        CentralizedError error = centralizedResponse.getErrors().get(0);
        assertNull(error.getCode());
        assertEquals("defaultMessage", error.getMessage());
    }

    @Test
    void centralizedErrorExceptionHandler_ShouldReturnCorrectStatus() {
        CentralizedException ex = new CentralizedException(
                new CentralizedError("CODE", "message"),
                HttpStatus.NOT_FOUND
        );

        ResponseEntity<?> response = exceptionHandler.centralizedErrorExceptionHandler(ex);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertInstanceOf(CentralizedResponse.class, response.getBody());
        CentralizedResponse<?> centralizedResponse = (CentralizedResponse<?>) response.getBody();
        assertNotNull(centralizedResponse);
        assertEquals(1, centralizedResponse.getErrors().size());
        assertEquals("CODE", centralizedResponse.getErrors().get(0).getCode());
        assertEquals("message", centralizedResponse.getErrors().get(0).getMessage());
    }

    @Test
    void handleParamMissing_ShouldReturnBadRequest() {
        MissingServletRequestParameterException ex = new MissingServletRequestParameterException("paramName", "paramType");

        ResponseEntity<?> response = exceptionHandler.handleParamMissing(ex);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertInstanceOf(CentralizedResponse.class, response.getBody());
        CentralizedResponse<?> centralizedResponse = (CentralizedResponse<?>) response.getBody();
        assertNotNull(centralizedResponse);
        assertEquals(1, centralizedResponse.getErrors().size());
        assertEquals(HttpStatus.UNPROCESSABLE_ENTITY.toString(), centralizedResponse.getErrors().get(0).getCode());
        assertTrue(centralizedResponse.getErrors().get(0).getMessage().contains("paramName"));
    }

    @Test
    void handleMissingRequestHeaderException_ShouldReturnUnauthorized() {
        when(missingRequestHeaderException.getMessage()).thenReturn("Required request header 'headerName' is not present");

        ResponseEntity<?> response = exceptionHandler.handleMissingRequestHeaderException(missingRequestHeaderException);

        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
        assertInstanceOf(CentralizedResponse.class, response.getBody());
        CentralizedResponse<?> centralizedResponse = (CentralizedResponse<?>) response.getBody();
        assertNotNull(centralizedResponse);
        assertEquals(1, centralizedResponse.getErrors().size());
        CentralizedError error = centralizedResponse.getErrors().get(0);
        assertEquals(HttpStatus.UNAUTHORIZED.toString(), error.getCode());
        assertEquals("Required request header 'headerName' is not present", error.getMessage());
    }

    @Test
    void nullPointerExceptionHandler_ShouldReturnInternalServerError() {
        NullPointerException ex = new NullPointerException("Null pointer occurred");

        ResponseEntity<?> response = exceptionHandler.nullPointerExceptionHandler(ex);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertInstanceOf(CentralizedResponse.class, response.getBody());
        CentralizedResponse<?> centralizedResponse = (CentralizedResponse<?>) response.getBody();
        assertNotNull(centralizedResponse);
        assertEquals(1, centralizedResponse.getErrors().size());
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR.toString(), centralizedResponse.getErrors().get(0).getCode());
        assertEquals("Null pointer occurred", centralizedResponse.getErrors().get(0).getMessage());
    }
}