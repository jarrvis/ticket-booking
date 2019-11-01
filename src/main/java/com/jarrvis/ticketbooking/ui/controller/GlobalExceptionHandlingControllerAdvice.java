package com.jarrvis.ticketbooking.ui.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.hateoas.VndErrors;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import javax.naming.ServiceUnavailableException;
import java.util.stream.Collectors;


/**
 * Performs exception handling for all REST API controllers. This class provides exception handlers that respond to
 * possible exceptions with appropriate HTTP status codes for the client.
 */
@Slf4j
@ControllerAdvice
public class GlobalExceptionHandlingControllerAdvice {


    /**
     * Exception handler for <i>MethodArgumentNotValidException</i>, translating error into HTTP status code 422 and returning
     * validation errors in the payload of the response.
     *
     * @param ex InvalidRequestException
     */
    @ResponseStatus(value = HttpStatus.UNPROCESSABLE_ENTITY)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseBody
    public VndErrors handleInvalidRequestException(MethodArgumentNotValidException ex) {
        log.error(ex.getMessage(), ex);
        if ((ex.getBindingResult().hasErrors())) {
            return new VndErrors(ex.getBindingResult().getAllErrors()
                    .stream()
                    .map(error -> new VndErrors.VndError(error.toString(),
                            error.getDefaultMessage()))
                    .collect(Collectors.toList()));

        }
        return null;
    }

    /**
     * General fallback exception handler, translating all not otherwise caught errors into HTTP status code 503.
     *
     * @param ex Exception
     */
    @ExceptionHandler(AccessDeniedException.class)
    @ResponseStatus(value = HttpStatus.UNAUTHORIZED)
    @ResponseBody
    public VndErrors handleAccessDeniedException(AccessDeniedException ex) {
        log.error(ex.getMessage(), ex);
        return new VndErrors("error", ex.getMessage());
    }

    /**
     * General fallback exception handler, translating all not otherwise caught errors into HTTP status code 503.
     *
     * @param ex Exception
     */
    @ExceptionHandler(ServiceUnavailableException.class)
    @ResponseStatus(value = HttpStatus.SERVICE_UNAVAILABLE)
    @ResponseBody
    public VndErrors handleServiceUnavailableException(ServiceUnavailableException ex) {
        log.error(ex.getMessage(), ex);
        return new VndErrors("error", ex.getMessage());
    }


    /**
     * General fallback exception handler, translating all not otherwise caught errors into HTTP status code 503.
     *
     * @param ex Exception
     */
    @ExceptionHandler(Exception.class)
    @ResponseStatus(value = HttpStatus.SERVICE_UNAVAILABLE)
    @ResponseBody
    public VndErrors handleException(Exception ex) {
        log.error(ex.getMessage(), ex);
        return new VndErrors("error", ex.getMessage());

    }

}
