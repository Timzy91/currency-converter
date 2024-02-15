package com.converter.currencies.service.exceptions;

public class NoSuchTransactionFoundException extends Exception {
    public NoSuchTransactionFoundException(String message) {
        super(message);
    }
}
