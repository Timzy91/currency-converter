package com.converter.currencies.service;

import com.converter.currencies.model.CurrencyConvertedTransaction;
import com.converter.currencies.model.ExchangeRateData;
import com.converter.currencies.model.Transaction;
import com.converter.currencies.service.exceptions.NoExchangeRateFoundException;
import com.converter.currencies.service.exceptions.NoSuchTransactionFoundException;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Optional;

@Service
@AllArgsConstructor
public class CurrencyConverterService {

    private final TransactionService transactionService;
    private final ExchangeRateService exchangeRateService;

    public CurrencyConvertedTransaction convertTransaction(Long id, String countryCurrencyDesc) throws NoSuchTransactionFoundException, NoExchangeRateFoundException {
        Transaction transaction = getTransaction(id);
        return fetchExchangeRateAndConvert(transaction, countryCurrencyDesc);
    }

    private CurrencyConvertedTransaction fetchExchangeRateAndConvert(Transaction transaction, String countryCurrencyDesc) throws NoExchangeRateFoundException {
        Optional<ExchangeRateData> exchangeRateData = exchangeRateService.fetchExchangeRate(countryCurrencyDesc, transaction.getTransactionDate());
        return exchangeRateData.map(data -> createCurrencyConvertedTransaction(data, transaction)).orElseThrow(() -> createNoExchangeRateFoundException(countryCurrencyDesc));
    }

    private Transaction getTransaction(Long id) throws NoSuchTransactionFoundException {
        Optional<Transaction> transaction = transactionService.getTransaction(id);
        return transaction.orElseThrow(() -> createNoSuchTransactionFoundException(id));
    }

    private CurrencyConvertedTransaction createCurrencyConvertedTransaction(ExchangeRateData exchangeRateData, Transaction transaction) {
        BigDecimal convertedAmount = transaction.getAmount().multiply(exchangeRateData.getExchangeRate()).setScale(2, RoundingMode.CEILING);
        return CurrencyConvertedTransaction
                .builder()
                .transaction(transaction)
                .convertedAmount(convertedAmount)
                .exchangeRate(exchangeRateData.getExchangeRate())
                .countryCurrencyDesc(exchangeRateData.getCountryCurrencyDesc())
                .build();
    }

    private NoSuchTransactionFoundException createNoSuchTransactionFoundException(Long id) {
        return new NoSuchTransactionFoundException(String.format("No purchase transaction found with id:%d", id));
    }

    private NoExchangeRateFoundException createNoExchangeRateFoundException(String countryCurrencyDesc) {
        return new NoExchangeRateFoundException(String.format("No exchange rate found for countryCurrencyDesc:%s", countryCurrencyDesc));
    }

}
