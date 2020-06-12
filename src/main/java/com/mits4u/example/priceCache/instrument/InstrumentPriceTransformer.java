package com.mits4u.example.priceCache.instrument;

import com.mits4u.example.priceCache.api.model.InstrumentPrice;
import com.mits4u.example.priceCache.instrument.db.Price;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;

@Component
public class InstrumentPriceTransformer {

    @NonNull
    public Price dtoToEntity(@NonNull InstrumentPrice instrumentPrice) {
        return Price.builder()
                .instrumentId(instrumentPrice.getInstrumentId())
                .vendorId(instrumentPrice.getVendorId())
                .price(instrumentPrice.getPrice())
                .priceTimestamp(instrumentPrice.getPriceTimestamp())
                .build();
    }

    @NonNull
    public InstrumentPrice entityToDto(@NonNull Price price) {
        return InstrumentPrice.builder()
                .instrumentId(price.getInstrumentId())
                .vendorId(price.getVendorId())
                .price(price.getPrice())
                .priceTimestamp(price.getPriceTimestamp())
                .build();
    }

}
