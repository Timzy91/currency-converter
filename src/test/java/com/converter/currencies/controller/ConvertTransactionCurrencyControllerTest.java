package com.converter.currencies.controller;

import com.converter.currencies.model.CurrencyConvertedTransaction;
import com.converter.currencies.model.Transaction;
import com.converter.currencies.service.CurrencyConverterService;
import com.converter.currencies.service.exceptions.NoExchangeRateFoundException;
import com.converter.currencies.service.exceptions.NoSuchTransactionFoundException;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;

import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@WebMvcTest(controllers = {ConvertTransactionCurrencyController.class})
public class ConvertTransactionCurrencyControllerTest {

    public static final String CONVERT_TRANSACTION_CURRENCY_API = "/api/v1/convert-transaction-currency/{id}";
    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private CurrencyConverterService currencyConverterService;

    @Test
    public void testConvertTransactionForCurrency_whenValidInput_thenReturns200() throws Exception {
        Transaction transaction = new Transaction(1L, "first test transaction",
                LocalDate.of(2023, 2, 14),
                new BigDecimal("90.99").setScale(2, RoundingMode.CEILING));

        Mockito.when(currencyConverterService.convertTransaction(anyLong(), anyString()))
                .thenReturn(CurrencyConvertedTransaction
                        .builder()
                        .transaction(transaction)
                        .countryCurrencyDesc("test-test")
                        .convertedAmount(new BigDecimal("999.75"))
                        .exchangeRate(new BigDecimal("83.74"))
                        .build());

        ResultActions result = mockMvc.perform(get(CONVERT_TRANSACTION_CURRENCY_API, 1L)
                .queryParam("countryCurrencyDesc", "test-test")
                .contentType(MediaType.APPLICATION_JSON));

        result.andExpect(MockMvcResultMatchers.status().is2xxSuccessful())
                .andExpect(jsonPath("$.transaction.id").value(1))
                .andExpect(jsonPath("$.transaction.description").value("first test transaction"))
                .andExpect(jsonPath("$.transaction.transactionDate").value("2023-02-14"))
                .andExpect(jsonPath("$.transaction.amount").value(90.99))
                .andExpect(jsonPath("$.countryCurrencyDesc").value("test-test"))
                .andExpect(jsonPath("$.convertedAmount").value("999.75"))
                .andExpect(jsonPath("$.exchangeRate").value("83.74"));

    }

    @Test
    public void testConvertTransactionForCurrency_whenInvalidInputField_thenReturns400() throws Exception {

        ResultActions result = mockMvc.perform(get(CONVERT_TRANSACTION_CURRENCY_API, "abc")
                .queryParam("countryCurrencyDesc", "test-test")
                .contentType(MediaType.APPLICATION_JSON));

        result.andExpect(MockMvcResultMatchers.status().isBadRequest());

    }

    @Test
    public void testConvertTransactionForCurrency_whenInvalidTransactionId_thenReturns404() throws Exception {
        Mockito.when(currencyConverterService.convertTransaction(anyLong(), anyString()))
                .thenThrow(new NoSuchTransactionFoundException(""));

        ResultActions result = mockMvc.perform(get(CONVERT_TRANSACTION_CURRENCY_API, 2L)
                .queryParam("countryCurrencyDesc", "test-test")
                .contentType(MediaType.APPLICATION_JSON));

        result.andExpect(MockMvcResultMatchers.status().isNotFound());

    }

    @Test
    public void testConvertTransactionForCurrency_whenNoExchangeRateDataFound_thenReturns500() throws Exception {
        Mockito.when(currencyConverterService.convertTransaction(anyLong(), anyString()))
                .thenThrow(new NoExchangeRateFoundException(""));

        ResultActions result = mockMvc.perform(get(CONVERT_TRANSACTION_CURRENCY_API, 2L)
                .queryParam("countryCurrencyDesc", "test-test")
                .contentType(MediaType.APPLICATION_JSON));

        result.andExpect(MockMvcResultMatchers.status().isInternalServerError())
                .andExpect(jsonPath("$.error").value("Purchase cannot be converted to target currency"));

    }
}
