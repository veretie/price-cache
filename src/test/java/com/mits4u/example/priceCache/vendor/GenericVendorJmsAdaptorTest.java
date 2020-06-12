package com.mits4u.example.priceCache.vendor;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mits4u.example.priceCache.TestStubs;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.jms.core.JmsTemplate;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class GenericVendorJmsAdaptorTest {

    @InjectMocks
    private GenericVendorJmsAdaptor jmsAdaptor;

    @Mock
    private JmsTemplate jmsTemplate;

    @Mock
    private ObjectMapper objectMapper;

    @Mock
    private InstrumentPriceValidator validator;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        jmsAdaptor.setVendorPricesQueue("mockVendorQueue");
    }


    @Test
    void validateAndSend__happyPath() throws JsonProcessingException {

        when(objectMapper.writeValueAsString(any())).thenReturn("{priceAsJsonString}");
        var price = TestStubs.stubApiPrice("P1", "BBG");
        jmsAdaptor.validateAndSend(price);

        verify(validator).validate(price);
        verify(jmsTemplate).convertAndSend("mockVendorQueue", "{priceAsJsonString}");

    }

    @Test
    void validateAndSend__failedSerialization() throws JsonProcessingException {

        when(objectMapper.writeValueAsString(any())).thenThrow(new JsonProcessingException("Simulated exception") {});
        var price = TestStubs.stubApiPrice("P1", "BBG");

        assertThatThrownBy(() -> jmsAdaptor.validateAndSend(price))
                .hasMessage("failed serialising")
                .isInstanceOf(RuntimeException.class);

    }

}