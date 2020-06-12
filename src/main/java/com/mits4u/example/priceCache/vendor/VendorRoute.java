package com.mits4u.example.priceCache.vendor;

import com.mits4u.example.priceCache.api.model.InstrumentPrice;
import com.mits4u.example.priceCache.instrument.db.Price;
import com.mits4u.example.priceCache.instrument.db.PriceDao;
import com.mits4u.example.priceCache.instrument.InstrumentPriceTransformer;
import org.apache.camel.LoggingLevel;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.model.dataformat.JsonLibrary;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class VendorRoute extends RouteBuilder {

    @Value("${priceCache.jms.endpoints.genericVendorQueue}")
    private String genericVendorQueueEndpoint;

    @Value("${priceCache.jms.endpoints.dlq}")
    private String dlqEndpoint;

    @Autowired
    private PriceDao priceDao;

    @Override
    public void configure() {

        errorHandler(deadLetterChannel(dlqEndpoint)
                .retryAttemptedLogLevel(LoggingLevel.ERROR));

        from(genericVendorQueueEndpoint)
            .routeId("vendor:prices")
            .transacted("PROPAGATION_REQUIRED")
                .log("received instrument price from vendor")
            .unmarshal()
                .json(JsonLibrary.Jackson, InstrumentPrice.class)
                .log("unmarshalled pricing data")
            .bean(InstrumentPriceValidator.class)
                .log("validated pricing data")
            .bean(InstrumentPriceTransformer.class, "dtoToEntity")
                .log("prepared price entity")
            .process()
                .body(Price.class, priceDao::save)
                .log("persisted price entity");
    }

}
