package com.converter.currencies.integration;

import com.converter.currencies.CurrenciesApplication;
import com.converter.currencies.service.ExchangeRateService;
import com.github.tomakehurst.wiremock.client.WireMock;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.cloud.contract.wiremock.AutoConfigureWireMock;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.reactive.server.WebTestClient;

@SpringBootTest(classes = CurrenciesApplication.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureWireMock(port = 8336)
@ActiveProfiles("it")
public class ConvertTransactionCurrencyControllerITest {

    private static final String CONVERT_TRANSACTION_CURRENCY_API = "/api/v1/convert-transaction-currency/{id}";
    @LocalServerPort
    private int testServerPort;
    private WebTestClient webTestClient;

    @BeforeEach
    public void setup() {
        webTestClient = WebTestClient.bindToServer().baseUrl("http://localhost:" + testServerPort).build();
    }

    @Test
    public void testRetrieveTransactionForCurrency_whenValidInput_thenReturns200() {

        WireMock.stubFor(WireMock.get(WireMock.urlPathMatching(ExchangeRateService.EXCHANGE_RATE_API_CONSTANTS.EXCHANGE_RATE_API_ENDPOINT))
                .willReturn(WireMock.aResponse().withBody(createTestResponse())));

        WebTestClient.ResponseSpec response = webTestClient.get()
                .uri(uriBuilder -> uriBuilder.path(CONVERT_TRANSACTION_CURRENCY_API)
                        .queryParam("countryCurrencyDesc", "test-test")
                        .build(2001L))
                .exchange();

        response.expectStatus().is2xxSuccessful().expectBody().json("""
                {
                    "transaction": {
                        "id": 2001,
                        "description": "thousand first transaction",
                        "transactionDate": "2023-12-13",
                        "amount": 40.47
                    },
                    "exchangeRate": 10,
                    "convertedAmount": 404.7,
                    "countryCurrencyDesc": "test-test"
                }
                """);
    }

    private String createTestResponse() {
        return """
                {
                    "data": [
                        {
                            "country_currency_desc": "test-test",
                            "exchange_rate": "10",
                            "record_date": "2023-12-31"
                        }
                    ],
                    "meta": {
                        "count": 1,
                        "labels": {
                            "country_currency_desc": "Country - Currency Description",
                            "exchange_rate": "Exchange Rate",
                            "record_date": "Record Date"
                        },
                        "dataTypes": {
                            "country_currency_desc": "STRING",
                            "exchange_rate": "NUMBER",
                            "record_date": "DATE"
                        },
                        "dataFormats": {
                            "country_currency_desc": "String",
                            "exchange_rate": "10.2",
                            "record_date": "YYYY-MM-DD"
                        },
                        "total-count": 73,
                        "total-pages": 73
                    },
                    "links": {
                        "self": "&page%5Bnumber%5D=1&page%5Bsize%5D=1",
                        "first": "&page%5Bnumber%5D=1&page%5Bsize%5D=1",
                        "prev": null,
                        "next": "&page%5Bnumber%5D=2&page%5Bsize%5D=1",
                        "last": "&page%5Bnumber%5D=73&page%5Bsize%5D=1"
                    }
                }
                """;
    }

}
