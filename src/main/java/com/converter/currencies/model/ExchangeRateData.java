package com.converter.currencies.model;

import com.converter.currencies.config.ExchangeRateDeserializer;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

import java.math.BigDecimal;
import java.time.LocalDate;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class ExchangeRateData {
    @JsonProperty("country_currency_desc")
    private String countryCurrencyDesc;
    @JsonProperty("exchange_rate")
    @JsonDeserialize(using = ExchangeRateDeserializer.class)
    private BigDecimal exchangeRate;
    @JsonProperty("record_date")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate recordDate;
}
