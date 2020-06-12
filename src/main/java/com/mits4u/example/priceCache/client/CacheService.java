package com.mits4u.example.priceCache.client;

import com.mits4u.example.priceCache.api.model.InstrumentPrice;
import com.mits4u.example.priceCache.instrument.InstrumentPriceTransformer;
import com.mits4u.example.priceCache.instrument.db.PriceDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.stream.Collectors;

@Service
public class CacheService {

    @Autowired
    private PriceDao priceDao;

    @Autowired
    private InstrumentPriceTransformer transformer;

    public Collection<InstrumentPrice> getPricesFromVendor(String vendorId) {
        var instrumentPrices = priceDao.findVendorPrices(vendorId);
        return instrumentPrices.stream()
                .map(transformer::entityToDto)
                .collect(Collectors.toList());
    }

    public Collection<InstrumentPrice> getPricesForInstrument(String instrumentId) {
        var instrumentPrices = priceDao.findInstrumentPrices(instrumentId);
        return instrumentPrices.stream()
                .map(transformer::entityToDto)
                .collect(Collectors.toList());
    }
}
