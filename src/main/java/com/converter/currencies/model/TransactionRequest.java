package com.converter.currencies.model;

import com.converter.currencies.config.TransactionAmountDeserializer;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

import java.math.BigDecimal;
import java.time.LocalDate;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class TransactionRequest {
    @Size(max = 50) @NotBlank
    String description;
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @NotNull
    LocalDate transactionDate;
    @NotNull
    @JsonDeserialize(using = TransactionAmountDeserializer.class)
    BigDecimal amount;
}
