package com.converter.currencies.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "transactions")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Transaction {

    @Id
    @GeneratedValue
    Long id;

    @Size(max = 50) @NotBlank
    String description;

    @Column(name = "transaction_date", columnDefinition = "DATE")
    @NotNull
    LocalDate transactionDate;

    @NotNull
    BigDecimal amount;

}
