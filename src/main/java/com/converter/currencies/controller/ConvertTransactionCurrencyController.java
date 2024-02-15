package com.converter.currencies.controller;

import com.converter.currencies.model.CurrencyConvertedTransaction;
import com.converter.currencies.service.CurrencyConverterService;
import com.converter.currencies.service.exceptions.NoExchangeRateFoundException;
import com.converter.currencies.service.exceptions.NoSuchTransactionFoundException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/convert-transaction-currency")
@AllArgsConstructor
public class ConvertTransactionCurrencyController {

    private final CurrencyConverterService currencyConverterService;

    @GetMapping("/{id}")
    @Operation(summary = "Retrieve a purchase transaction with amount converted to a specified country's currency")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Found and converted purchase transaction to desired currency",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = CurrencyConvertedTransaction.class))}),
            @ApiResponse(responseCode = "400", description = "Invalid id",
                    content = @Content),
            @ApiResponse(responseCode = "404", description = "Purchase transaction not found",
                    content = @Content),
            @ApiResponse(responseCode = "500", description = "Purchase transaction could not be converted to target currency",
                    content = @Content)
    })
    public CurrencyConvertedTransaction convertTransactionForCurrency(
            @Parameter(description = "The transaction id to be converted for a currency")
            @PathVariable Long id,
            @Parameter(description = "The country currency to convert to eg: Canada-Dollar, India-Rupee")
            @RequestParam("countryCurrencyDesc")
            @Valid @NotNull String countryCurrencyDesc)
            throws NoSuchTransactionFoundException, NoExchangeRateFoundException {
        return currencyConverterService.convertTransaction(id, countryCurrencyDesc);
    }
}

