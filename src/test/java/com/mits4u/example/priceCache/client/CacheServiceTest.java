package com.mits4u.example.priceCache.client;

import com.mits4u.example.priceCache.TestStubs;
import com.mits4u.example.priceCache.api.model.InstrumentPrice;
import com.mits4u.example.priceCache.instrument.InstrumentPriceTransformer;
import com.mits4u.example.priceCache.instrument.db.Price;
import com.mits4u.example.priceCache.instrument.db.PriceDao;
import org.assertj.core.api.Assertions;
import org.assertj.core.util.Lists;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

class CacheServiceTest {

    @InjectMocks
    private CacheService cacheService;

    @Mock
    private PriceDao priceDao;

    @Mock
    private InstrumentPriceTransformer transformer;

    private Price entity;
    private InstrumentPrice dto;

    @BeforeEach
    public void setUp() {

        MockitoAnnotations.initMocks(this);

        entity = TestStubs.stubPrice("P1", "HL");
        dto = TestStubs.stubApiPrice("P1", "HL");

        when(priceDao.findVendorPrices(any())).thenReturn(Lists.newArrayList(entity));
        when(priceDao.findInstrumentPrices(any())).thenReturn(Lists.newArrayList(entity));
        when(transformer.entityToDto(entity)).thenReturn(dto);
    }

    @Test
    void getPricesFromVendor_emptyCollection() {

        when(priceDao.findVendorPrices("HL")).thenReturn(Lists.emptyList());
        var prices = cacheService.getPricesFromVendor("HL");

        Assertions.assertThat(prices).isEmpty();
        verifyNoInteractions(transformer);

    }

    @Test
    void getPricesFromVendor() {
        var prices = cacheService.getPricesFromVendor("HL");
        Assertions.assertThat(prices).containsExactly(dto);
    }

    @Test
    void getPricesForInstrument__emptyCollection() {

        when(priceDao.findInstrumentPrices("AMZN")).thenReturn(Lists.emptyList());
        var prices = cacheService.getPricesForInstrument("AMZN");

        Assertions.assertThat(prices).isEmpty();
        verifyNoInteractions(transformer);
    }

    @Test
    void getPricesForInstrument() {
        var prices = cacheService.getPricesForInstrument("AMZN");
        Assertions.assertThat(prices).containsExactly(dto);
    }
}