package com.mits4u.example.priceCache;

import com.mits4u.example.priceCache.api.model.InstrumentPrice;
import com.mits4u.example.priceCache.instrument.db.Price;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class TestStubs {

    public static InstrumentPrice stubApiPrice(String instrumentId, String vendorId) {
        return InstrumentPrice.builder()
                .instrumentId(instrumentId)
                .vendorId(vendorId)
                .price(new BigDecimal("1.01"))
                .priceTimestamp(LocalDateTime.of(2020, 01, 02, 03, 04, 05))
                .build();
    }

    public static InstrumentPrice stubInvalidApiPrice(String instrumentId) {
        return InstrumentPrice.builder()
                .instrumentId(instrumentId)
                .price(new BigDecimal("1.01"))
                .build();
    }

    public static Price stubPrice(String instrumentId, String vendorId) {
        return Price.builder()
                .id(1)
                .instrumentId(instrumentId)
                .vendorId(vendorId)
                .price(new BigDecimal("1.01"))
                .priceTimestamp(LocalDateTime.of(2020, 01, 02, 03, 04, 05))
                .build();
    }

}
