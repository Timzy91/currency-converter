package com.converter.currencies.config;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;

@Component
public class TransactionAmountDeserializer extends JsonDeserializer<BigDecimal> {
    @Override
    public BigDecimal deserialize(JsonParser jsonParser,
                                  DeserializationContext deserializationContext) throws IOException {
        return new BigDecimal(jsonParser.getValueAsString()).setScale(2, RoundingMode.CEILING);
    }
}
