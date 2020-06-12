package com.mits4u.example.priceCache.vendor;

import com.mits4u.example.priceCache.TestStubs;
import com.mits4u.example.priceCache.api.model.InstrumentPrice;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class InstrumentPriceValidatorTest {

    private InstrumentPriceValidator validator = new InstrumentPriceValidator();

    private InstrumentPrice price;

    @BeforeEach
    public void setUp() {
        price = TestStubs.stubApiPrice("P1", "HL");
    }

    @Test
    void validate() {
        validator.validate(price);
    }

    @Test
    void validate_missingInstrumentId() {
        price.setInstrumentId(null);
        Assertions.assertThatThrownBy(() -> validator.validate(price))
                .isInstanceOf(NullPointerException.class)
                .hasMessage("instrument id is missing");
    }

    @Test
    void validate_missingPrice() {
        price.setPrice(null);
        Assertions.assertThatThrownBy(() -> validator.validate(price))
                .isInstanceOf(NullPointerException.class)
                .hasMessage("price is missing");
    }

    @Test
    void validate_missingTimestamp() {
        price.setPriceTimestamp(null);
        Assertions.assertThatThrownBy(() -> validator.validate(price))
                .isInstanceOf(NullPointerException.class)
                .hasMessage("timestamp is missing");
    }

    @Test
    void validate_missingVendorId() {
        price.setVendorId(null);
        Assertions.assertThatThrownBy(() -> validator.validate(price))
                .isInstanceOf(NullPointerException.class)
                .hasMessage("vendor id is missing");
    }

}