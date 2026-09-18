package com.rolandhuon.clashofclans.handler;

import com.rolandhuon.clashofclans.dto.ErrorResponse;
import com.rolandhuon.clashofclans.service.InsufficientResourcesException;
import com.rolandhuon.clashofclans.service.NotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.util.stream.Collectors;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(IllegalArgumentException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse onIllegalArgument(IllegalArgumentException e){
        return new ErrorResponse(400, describe(e, "The request could not be accepted."));
    }

    @ExceptionHandler(IllegalStateException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse onIllegalState(IllegalStateException e){
        return new ErrorResponse(400, describe(e, "The request could not be accepted."));
    }

    @ExceptionHandler(NotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse onNotFound(NotFoundException e){
        return new ErrorResponse(404, describe(e, "Not found."));
    }

    @ExceptionHandler(InsufficientResourcesException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public ErrorResponse onInsufficientResources(InsufficientResourcesException e){
        return new ErrorResponse(409, describe(e, "The request conflicts with the current state."));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse onInvalidPayload(MethodArgumentNotValidException e){
        String details = e.getBindingResult().getFieldErrors().stream()
                .map(error -> error.getField() + " " + error.getDefaultMessage())
                .sorted()
                .collect(Collectors.joining(", "));

        return new ErrorResponse(400, "Invalid request: " + details);
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse onTypeMismatch(MethodArgumentTypeMismatchException e){
        return new ErrorResponse(400, "Invalid value for '" + e.getName() + "': " + e.getValue());
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse onUnreadableBody(HttpMessageNotReadableException e){
        return new ErrorResponse(400, "Malformed JSON body.");
    }

    private static String describe(Exception e, String fallback) {
        String message = e.getMessage();
        return (message == null || message.isBlank()) ? fallback : message;
    }
}
