package com.converter.currencies.mapper;

import com.converter.currencies.model.Transaction;
import com.converter.currencies.model.TransactionRequest;
import org.springframework.stereotype.Component;

@Component
public class TransactionRequestMapper {
    public Transaction map(TransactionRequest transactionRequest) {
        return new Transaction(null, transactionRequest.getDescription(),
                transactionRequest.getTransactionDate(), transactionRequest.getAmount());
    }
}
