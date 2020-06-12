package com.mits4u.example.priceCache.vendor;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mits4u.example.priceCache.api.model.InstrumentPrice;
import lombok.Setter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class GenericVendorJmsAdaptor {

    private Logger LOGGER = LoggerFactory.getLogger(GenericVendorJmsAdaptor.class);

    @Setter
    @Value("${priceCache.jms.queues.vendorPrices}")
    private String vendorPricesQueue;

    @Autowired
    private JmsTemplate jmsTemplate;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private InstrumentPriceValidator vendorPriceValidator;

    @Transactional
    public void validateAndSend(InstrumentPrice instrumentPrice) {

        vendorPriceValidator.validate(instrumentPrice);

        LOGGER.info("sending {} to '{}'", instrumentPrice, vendorPricesQueue);
        jmsTemplate.convertAndSend(vendorPricesQueue, serialise(instrumentPrice));

    }

    private String serialise(Object o) {
        try {
            return objectMapper.writeValueAsString(o);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("failed serialising", e);
        }
    }
}
