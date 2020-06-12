package com.mits4u.example.priceCache.instrument;

import com.mits4u.example.priceCache.TestStubs;
import com.mits4u.example.priceCache.api.model.InstrumentPrice;
import com.mits4u.example.priceCache.instrument.db.Price;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

class InstrumentPriceTransformerTest {

    @InjectMocks
    private InstrumentPriceTransformer transformer;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    void dtoToEntity() {
        var entity = transformer.dtoToEntity(TestStubs.stubApiPrice("P1", "BBG"));
        assertThat(entity.getId()).isZero();
        assertThat(entity.getInstrumentId()).isEqualTo("P1");
        assertThat(entity.getPrice()).isEqualTo(new BigDecimal("1.01"));
        assertThat(entity.getPriceTimestamp()).isEqualTo(LocalDateTime.of(2020, 01, 02, 03, 04, 05));
        assertThat(entity.getVendorId()).isEqualTo("BBG");
    }


    @Test
    void dtoToEntity_empty() {
        var entity = transformer.dtoToEntity(new InstrumentPrice());
        assertThat(entity.getId()).isZero();
        assertThat(entity.getInstrumentId()).isNull();
        assertThat(entity.getPrice()).isNull();
        assertThat(entity.getPriceTimestamp()).isNull();
        assertThat(entity.getVendorId()).isNull();
    }

    @Test
    void entityToDto() {
        var dto = transformer.entityToDto(TestStubs.stubPrice("P1", "BBG"));
        assertThat(dto.getInstrumentId()).isEqualTo("P1");
        assertThat(dto.getPrice()).isEqualTo(new BigDecimal("1.01"));
        assertThat(dto.getPriceTimestamp()).isEqualTo(LocalDateTime.of(2020, 01, 02, 03, 04, 05));
        assertThat(dto.getVendorId()).isEqualTo("BBG");
    }


    @Test
    void entityToDto_empty() {
        var dto = transformer.entityToDto(new Price());
        assertThat(dto.getInstrumentId()).isNull();
        assertThat(dto.getPrice()).isNull();
        assertThat(dto.getPriceTimestamp()).isNull();
        assertThat(dto.getVendorId()).isNull();
    }

}