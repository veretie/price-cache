package com.mits4u.example.priceCache;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mits4u.example.priceCache.api.model.InstrumentPrice;
import com.mits4u.example.priceCache.instrument.db.PriceRepository;
import org.apache.camel.ConsumerTemplate;
import org.apache.camel.EndpointInject;
import org.apache.camel.ProducerTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import org.springframework.stereotype.Component;

@Component
public class IntegrationTestHelper {

    private static final String TEST_PRICE_METADATA_HEADER = "instrumentId";

    @Value("${priceCache.jms.endpoints.dlq}")
    private String dlqEndpoint;

    @Value("${priceCache.jms.endpoints.genericVendorQueue}")
    private String genericVendorEndpoint;

    @Autowired
    private PriceRepository priceRepository;

    @Autowired
    @EndpointInject
    private ProducerTemplate jmsProducer;

    @Autowired
    @EndpointInject
    private ConsumerTemplate jmsConsumer;

    @Autowired
    private ObjectMapper objectMapper;

    public boolean priceRecordedInDb(String instrumentId) {
        var prices = priceRepository.findAllByInstrumentId(instrumentId);
        return prices.stream()
                .filter(p -> p.getId() != 0)
                .filter(p -> p.getInstrumentId().equals(instrumentId))
                .findFirst().isPresent();
    }

    public boolean messageInDlq(String instrumentId, long timeout) {

        var exchange = jmsConsumer.receive(dlqEndpoint, timeout);
        var resolvedInstrumentId = exchange.getMessage().getHeader(TEST_PRICE_METADATA_HEADER, String.class);
        return instrumentId.equals(resolvedInstrumentId);

    }

    public void sendVendorJmsMessage(InstrumentPrice instrumentPrice) throws JsonProcessingException {
        jmsProducer.sendBodyAndHeader(
                genericVendorEndpoint,
                objectMapper.writeValueAsString(instrumentPrice),
                TEST_PRICE_METADATA_HEADER,
                instrumentPrice.getInstrumentId());
    }

    public void sendInvalidFormatMessage(String instrumentId) {
        jmsProducer.sendBodyAndHeader(
                genericVendorEndpoint,
                new Object(),
                TEST_PRICE_METADATA_HEADER,
                instrumentId);
    }
}
