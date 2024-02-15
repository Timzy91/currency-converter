package com.converter.currencies.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class CurrencyConvertedTransaction {
    Transaction transaction;
    BigDecimal exchangeRate;
    BigDecimal convertedAmount;
    String countryCurrencyDesc;

}
