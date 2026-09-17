package com.rolandhuon.clashofclans.handler;

import com.rolandhuon.clashofclans.dto.ErrorResponse;
import com.rolandhuon.clashofclans.service.InsufficientResourcesException;
import com.rolandhuon.clashofclans.service.NotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(IllegalArgumentException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse onIllegalArgument(IllegalArgumentException e){
        return new ErrorResponse(400, e.getMessage());
    }

    @ExceptionHandler(IllegalStateException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse onIllegalState(IllegalStateException e){
        return new ErrorResponse(400, e.getMessage());
    }

    @ExceptionHandler(NotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse onNotFound(NotFoundException e){
        return new ErrorResponse(404, e.getMessage());
    }

    @ExceptionHandler(InsufficientResourcesException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public ErrorResponse onInsufficientResources(InsufficientResourcesException e){
        return new ErrorResponse(409, e.getMessage());
    }
}
