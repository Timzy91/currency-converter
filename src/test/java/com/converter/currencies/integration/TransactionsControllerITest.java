package com.converter.currencies.integration;

import com.converter.currencies.CurrenciesApplication;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.reactive.server.WebTestClient;

@SpringBootTest(classes = CurrenciesApplication.class,
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT
)
@ActiveProfiles("it")
public class TransactionsControllerITest {

    private static final String TRANSACTIONS_API = "/api/v1/transactions";
    @LocalServerPort
    private int port;
    private WebTestClient webTestClient;

    @BeforeEach
    public void setup() {
        webTestClient = WebTestClient.bindToServer().baseUrl("http://localhost:" + port).build();
    }

    @Test
    public void testPostTransaction_whenValidInput_thenReturns201() {
        String transactionRequest = "{ \"description\": \"first test transaction\", \"transactionDate\": \"2023-02-14\"" +
                ", \"amount\": 90.983 }";

        WebTestClient.ResponseSpec response = webTestClient.post()
                .uri(TRANSACTIONS_API)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(transactionRequest)
                .exchange();

        response.expectStatus().isCreated()
                .expectBody()
                .jsonPath("$.id").isEqualTo(1)
                .jsonPath("$.description").isEqualTo("first test transaction")
                .jsonPath("transactionDate").isEqualTo("2023-02-14")
                .jsonPath("$.amount").isEqualTo(90.99);
    }
}
