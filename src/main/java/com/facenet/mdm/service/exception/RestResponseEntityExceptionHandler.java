package com.facenet.mdm.service.exception;

import com.facenet.mdm.service.dto.response.CommonResponse;
import java.util.HashMap;
import java.util.Map;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.InsufficientAuthenticationException;
import org.springframework.security.web.csrf.CsrfException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@ControllerAdvice
public class RestResponseEntityExceptionHandler extends ResponseEntityExceptionHandler {

    @Autowired
    MessageSource messageSource;

    private Logger logger = LogManager.getLogger(RestResponseEntityExceptionHandler.class);

    @ExceptionHandler(value = { CustomException.class })
    protected ResponseEntity<Object> handleCustomException(CustomException ex, WebRequest request) {
        String errorMsg = messageSource.getMessage(ex.getErrorCode(), ex.getArgs(), ex.getErrorCode(), request.getLocale());
        return ResponseEntity
            .status(ex.getHttpStatus())
            .body(new CommonResponse<>().errorCode(ex.getErrorCode()).message(errorMsg).isOk(false));
    }

    @Override
    public ResponseEntity<Object> handleMethodArgumentNotValid(
        MethodArgumentNotValidException ex,
        HttpHeaders headers,
        HttpStatus status,
        WebRequest request
    ) {
        Map<String, String> errors = new HashMap<>();

        ex
            .getBindingResult()
            .getAllErrors()
            .forEach(error -> {
                String errorCode = error.getDefaultMessage();
                String errorMsg = messageSource.getMessage(errorCode, null, errorCode, request.getLocale());
                errors.put(errorCode, errorMsg);
            });
        String errorList = errors.keySet().stream().reduce("", (s, s2) -> s + "," + s2);
        String errMsgList = errors.values().stream().reduce("", (s, s2) -> s + "," + s2);

        return ResponseEntity.badRequest().body(new CommonResponse<>().errorCode(errorList).message(errMsgList).isOk(false));
    }

    @Override
    protected ResponseEntity<Object> handleHttpMessageNotReadable(
        HttpMessageNotReadableException ex,
        HttpHeaders headers,
        HttpStatus status,
        WebRequest request
    ) {
        return ResponseEntity.badRequest().body(new CommonResponse<>().errorCode("invalid.param").message(ex.getMessage()).isOk(false));
    }

    @ExceptionHandler(value = { RuntimeException.class, Exception.class })
    protected ResponseEntity<Object> handleInternalException(Exception ex, WebRequest request) {
        logger.error("err", ex);
        if (ex instanceof CsrfException) {
            logger.error("invalid token or token is empty");
            return ResponseEntity
                .status(HttpStatus.UNAUTHORIZED)
                .body(new CommonResponse<>().errorCode("unauthorized").message("invalid token or token is empty"));
        } else if (ex instanceof AccessDeniedException || ex instanceof InsufficientAuthenticationException) {
            logger.error("access denied");
            return ResponseEntity
                .status(HttpStatus.UNAUTHORIZED)
                .body(
                    new CommonResponse<>()
                        .errorCode("access.denied")
                        .message(messageSource.getMessage("access.denied", null, request.getLocale()))
                );
        }
        String errorMsg = messageSource.getMessage("internal.error", null, request.getLocale());
        return ResponseEntity.internalServerError().body(new CommonResponse<>().errorCode("internal.error").message(errorMsg).isOk(false));
    }
}
