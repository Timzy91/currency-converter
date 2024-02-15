package com.converter.currencies.controller;

import com.converter.currencies.model.Transaction;
import com.converter.currencies.model.TransactionRequest;
import com.converter.currencies.service.TransactionService;
import jakarta.persistence.NoResultException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = {TransactionsController.class})
public class TransactionsControllerTest {

    public static final String TRANSACTIONS_API = "/api/v1/transactions";
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private TransactionService transactionService;

    @Test
    public void postTransaction_whenValidInput_thenReturns201() throws Exception {
        String transactionRequest = "{ \"description\": \"first test transaction\", \"transactionDate\": \"2023-02-14\"" +
                ", \"amount\": 90.983 }";
        when(transactionService.storeTransaction(any(TransactionRequest.class)))
                .thenReturn(new Transaction(1L, "first test transaction",
                        LocalDate.of(2023, 2, 14), new BigDecimal("90.99").setScale(2, RoundingMode.CEILING)));

        ResultActions result = mockMvc.perform(MockMvcRequestBuilders
                .post(TRANSACTIONS_API)
                .contentType(MediaType.APPLICATION_JSON)
                .content(transactionRequest));

        result.andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.description").value("first test transaction"))
                .andExpect(jsonPath("$.transactionDate").value("2023-02-14"))
                .andExpect(jsonPath("$.amount").value(90.99));
    }

    @Test
    public void postTransaction_whenInvalidInputField_thenReturns400() throws Exception {
        String transactionRequest = "{ \"abc\": \"first test transaction\", \"transactionDate\": \"2023-02-14\"" +
                ", \"amount\": 90.983 }";

        ResultActions result = mockMvc.perform(MockMvcRequestBuilders
                .post(TRANSACTIONS_API)
                .contentType(MediaType.APPLICATION_JSON)
                .content(transactionRequest));

        result.andExpect(status().isBadRequest());
    }

    @Test
    public void postTransaction_whenServiceError_thenReturns500() throws Exception {
        String transactionRequest = "{ \"description\": \"first test transaction\", \"transactionDate\": \"2023-02-14\"" +
                ", \"amount\": 90.983 }";

        when(transactionService.storeTransaction(any(TransactionRequest.class))).thenThrow(new NoResultException("Service down"));

        ResultActions result = mockMvc.perform(MockMvcRequestBuilders
                .post(TRANSACTIONS_API)
                .contentType(MediaType.APPLICATION_JSON)
                .content(transactionRequest));

        result.andExpect(status().isInternalServerError()).andReturn().getResponse().getContentAsString();
    }

}
