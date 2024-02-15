package com.converter.currencies.service;

import com.converter.currencies.mapper.TransactionRequestMapper;
import com.converter.currencies.model.Transaction;
import com.converter.currencies.model.TransactionRequest;
import com.converter.currencies.repository.TransactionRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@AllArgsConstructor
public class TransactionService {

    private final TransactionRequestMapper transactionRequestMapper;
    private final TransactionRepository transactionRepository;

    public Transaction storeTransaction(TransactionRequest transactionRequest) {
        Transaction transaction = transactionRequestMapper.map(transactionRequest);
        return transactionRepository.save(transaction);
    }

    public Optional<Transaction> getTransaction(Long id) {
        return transactionRepository.findById(id);
    }
}
