package com.converter.currencies.exceptions;

import com.converter.currencies.service.exceptions.NoExchangeRateFoundException;
import com.converter.currencies.service.exceptions.NoSuchTransactionFoundException;
import jakarta.persistence.NoResultException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.util.Map;

@ControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

    private final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(NoResultException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public void handleNoResultException(Exception ex) {
        logger.error("Internal error occurred ", ex);
    }

    @ExceptionHandler(NoSuchTransactionFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public void handleNoSuchTransactionFoundException() {
        logger.error("Transaction Not found");
    }

    @ExceptionHandler(NoExchangeRateFoundException.class)
    public ResponseEntity<Map<String, String>> handleNoExchangeRateFoundException(Exception ex) {
        Map<String, String> errorMap = Map.of("error", "Purchase cannot be converted to target currency");
        logger.error("Purchase cannot be converted to target currency", ex);
        return new ResponseEntity<>(errorMap, HttpStatus.INTERNAL_SERVER_ERROR);
    }


}
