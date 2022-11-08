package com.example.soccergroup.controller.exceptionHandler;

import com.example.soccergroup.controller.dto.ErrorMsgDto;
import com.example.soccergroup.exception.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.bind.support.WebExchangeBindException;

import java.util.Date;

@RestControllerAdvice
@Slf4j
public class RestExceptionHandler {
    @ExceptionHandler(SoccerGroupNotFoundException.class)
    ResponseEntity soccerGroupNotFoundException(SoccerGroupNotFoundException ex) {
        log.debug("handling exception::" + ex);

        return new ResponseEntity(new ErrorMsgDto(new Date(), "not found soccer group!", ex.getMessage()),
                HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(UnAuthorizedActionException.class)
    ResponseEntity unAuthorizedActionException(UnAuthorizedActionException ex) {
        log.debug("handling exception::" + ex);

        return new ResponseEntity(new ErrorMsgDto(new Date(), "unAuthorized to perform the action", ex.getMessage()),
                HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    ResponseEntity resourceNotFoundException(ResourceNotFoundException ex) {
        log.debug("handling exception::" + ex);

        return new ResponseEntity(new ErrorMsgDto(new Date(), "requested resource not found", ex.getMessage()),
                HttpStatus.NOT_FOUND);
    }



    @ExceptionHandler(JoinRequestCreationException.class)
    ResponseEntity JoinRequestCreationException(JoinRequestCreationException ex) {
        log.debug("handling exception::" + ex);

        return new ResponseEntity(new ErrorMsgDto(new Date(), "cannot create join request!", ex.getMessage()),
                HttpStatus.CONFLICT);
    }

    @ExceptionHandler(JoinRequestApproveException.class)
    ResponseEntity JoinRequestApproveException(JoinRequestApproveException ex) {
        log.debug("handling exception::" + ex);

        return new ResponseEntity(new ErrorMsgDto(new Date(), "error occurred in join request", ex.getMessage()),
                HttpStatus.CONFLICT);
    }



    @ExceptionHandler({WebExchangeBindException.class})
    ResponseEntity webExchangeBindException(WebExchangeBindException ex)
    {
        String rejectedValue = ex.getBindingResult().getFieldError().getRejectedValue() == null? ""
                : ex.getBindingResult().getFieldError().getRejectedValue().toString();
        String message = ex.getBindingResult().getFieldError().getDefaultMessage() == null? ""
                :ex.getBindingResult().getFieldError().getDefaultMessage();

        return new ResponseEntity(
                new ErrorMsgDto(new Date(), "bad request ", rejectedValue
                        + " is not valid with message : "
                        + message),
                HttpStatus.BAD_REQUEST
        );
    }
}
