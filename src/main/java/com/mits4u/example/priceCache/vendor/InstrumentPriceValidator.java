package com.mits4u.example.priceCache.vendor;

import com.mits4u.example.priceCache.api.model.InstrumentPrice;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;

import static com.google.common.base.Preconditions.checkNotNull;

@Component
public class InstrumentPriceValidator {

    public void validate(@NonNull InstrumentPrice instrumentPrice) {
        checkNotNull(instrumentPrice, "instrumentPrice object cannot be null");
        checkNotNull(instrumentPrice.getInstrumentId(), "instrument id is missing");
        checkNotNull(instrumentPrice.getVendorId(), "vendor id is missing");
        checkNotNull(instrumentPrice.getPrice(), "price is missing");
        checkNotNull(instrumentPrice.getPriceTimestamp(), "timestamp is missing");
    }

}
