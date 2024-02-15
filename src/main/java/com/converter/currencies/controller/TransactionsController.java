package com.converter.currencies.controller;

import com.converter.currencies.model.Transaction;
import com.converter.currencies.model.TransactionRequest;
import com.converter.currencies.service.TransactionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/transactions")
@AllArgsConstructor
public class TransactionsController {

    private final TransactionService transactionService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Store a purchase transaction in US Dollars")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Purchase transaction stored successfully",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = Transaction.class))}),
            @ApiResponse(responseCode = "400", description = "Invalid transaction request body",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = TransactionRequest.class))})
    })
    public Transaction postTransaction(@RequestBody @Valid TransactionRequest transactionRequest) {
        return transactionService.storeTransaction(transactionRequest);
    }

}
