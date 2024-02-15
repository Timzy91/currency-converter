package com.converter.currencies.service;

import com.converter.currencies.model.ExchangeRateData;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.UriBuilder;

import java.io.IOException;
import java.net.URI;
import java.time.LocalDate;
import java.util.Optional;

@Service
public class ExchangeRateService {

    private final WebClient webClient;
    private final ObjectMapper objectMapper;
    private final Logger logger;

    @Autowired
    public ExchangeRateService(@Value("${exchangeRateApiBaseUrl}") String exchangeRateApiBaseUrl) {
        this.logger = LoggerFactory.getLogger(ExchangeRateService.class);
        this.objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
        this.webClient = WebClient.builder().baseUrl(exchangeRateApiBaseUrl).build();
    }

    public Optional<ExchangeRateData> fetchExchangeRate(String countryCurrencyDesc, LocalDate transactionDate) {
        LocalDate sixMonthsBeforePurchaseDate = transactionDate.minusMonths(6);
        return fetchExchangeRateWithinLastSixMonthsOfTransaction(countryCurrencyDesc, sixMonthsBeforePurchaseDate,
                transactionDate);
    }

    private Optional<ExchangeRateData> fetchExchangeRateWithinLastSixMonthsOfTransaction(
            String countryCurrencyDesc, LocalDate sixMonthsBeforePurchaseDate, LocalDate transactionDate) {
        String response = webClient.get().uri(uriBuilder ->
                        buildExchangeRateAPIURI(uriBuilder, countryCurrencyDesc, sixMonthsBeforePurchaseDate, transactionDate))
                .retrieve().bodyToMono(String.class).block();

        return mapAPIResponseToExchangeRateData(response);
    }

    private Optional<ExchangeRateData> mapAPIResponseToExchangeRateData(String response) {
        try {
            JsonNode jsonNode = objectMapper.readTree(response);
            if ((!jsonNode.path("data").isNull()) && (jsonNode.path("meta").path("count").asInt() > 0)) {
                ExchangeRateData exchangeRateData = objectMapper.readValue(jsonNode.path("data").path(0).traverse(), ExchangeRateData.class);
                return Optional.of(exchangeRateData);
            } else if ((!jsonNode.path("data").isNull()) && (jsonNode.path("meta").path("count").asInt() > 0)) {
                logger.info("No data found for exchange rate api call: {}", jsonNode);
            } else {
                logger.error("Error occurred for exchange rate api call: {}", jsonNode);
            }
        } catch (IOException e) {
            logger.error("Error parsing exchange rate json data", e);
        }
        return Optional.empty();
    }

    private URI buildExchangeRateAPIURI(UriBuilder uriBuilder, String countryCurrencyDesc, LocalDate sixMonthsBeforePurchaseDate, LocalDate transactionDate) {
        String exchangeRateFilter = getExchangeRateFilter(countryCurrencyDesc, sixMonthsBeforePurchaseDate, transactionDate);
        return uriBuilder
                .path(EXCHANGE_RATE_API_CONSTANTS.EXCHANGE_RATE_API_ENDPOINT)
                .queryParam("fields", EXCHANGE_RATE_API_CONSTANTS.EXCHANGE_RATE_API_FIELDS_FILTER)
                .queryParam("sort", EXCHANGE_RATE_API_CONSTANTS.EXCHANGE_RATE_API_SORT_BY_DATE)
                .queryParam("format", EXCHANGE_RATE_API_CONSTANTS.EXCHANGE_RATE_API_DATA_FORMAT)
                .queryParam("page[size]", EXCHANGE_RATE_API_CONSTANTS.EXCHANGE_RATE_API_DATA_SIZE)
                .queryParam("filter", exchangeRateFilter)
                .build();
    }

    private String getExchangeRateFilter(String countryCurrencyDesc, LocalDate sixMonthsBeforePurchaseDate, LocalDate transactionDate) {
        return "%s%s%s%s%s%s".formatted(
                EXCHANGE_RATE_API_CONSTANTS.EXCHANGE_RATE_API_COUNTRY_CURRENCY_DESC_FILTER,
                countryCurrencyDesc,
                EXCHANGE_RATE_API_CONSTANTS.EXCHANGE_RATE_API_DATE_GREATER_THAN_FILTER,
                sixMonthsBeforePurchaseDate,
                EXCHANGE_RATE_API_CONSTANTS.EXCHANGE_RATE_API_DATE_LESS_THAN_EQUAL_TO_FILTER,
                transactionDate);
    }

    public static final class EXCHANGE_RATE_API_CONSTANTS {
        public static final String EXCHANGE_RATE_API_ENDPOINT = "/v1/accounting/od/rates_of_exchange";
        public static final String EXCHANGE_RATE_API_FIELDS_FILTER = "country_currency_desc,exchange_rate,record_date";
        public static final String EXCHANGE_RATE_API_DATE_GREATER_THAN_FILTER = ",record_date:gte:";
        public static final String EXCHANGE_RATE_API_DATE_LESS_THAN_EQUAL_TO_FILTER = ",record_date:lte:";
        public static final String EXCHANGE_RATE_API_COUNTRY_CURRENCY_DESC_FILTER = "country_currency_desc:eq:";
        public static final String EXCHANGE_RATE_API_SORT_BY_DATE = "-record_date";
        public static final String EXCHANGE_RATE_API_DATA_FORMAT = "json";
        public static final String EXCHANGE_RATE_API_DATA_SIZE = "1";

    }

}
