package com.converter.currencies.service.exceptions;

public class NoExchangeRateFoundException extends Exception {

    public NoExchangeRateFoundException(String message) {
        super(message);
    }
}
