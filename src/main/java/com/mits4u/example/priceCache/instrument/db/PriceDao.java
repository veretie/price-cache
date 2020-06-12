package com.mits4u.example.priceCache.instrument.db;

import org.springframework.lang.NonNull;
import org.springframework.stereotype.Repository;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.Collection;

@Repository
public class PriceDao {

    @Resource
    private PriceRepository priceRepository;

    public void save(Price name) {
        priceRepository.save(name);
    }

    public void delete(Price name) {
        priceRepository.deleteById(name.getId());
    }

    @NonNull
    public Collection<Price> findPricesBefore(LocalDateTime timestamp) {
        return priceRepository.findAllWithCreationDateTimeBefore(timestamp);
    }

    @NonNull
    public Collection<Price> findInstrumentPrices(String instrumentId) {
        return priceRepository.findAllByInstrumentId(instrumentId);
    }

    @NonNull
    public Collection<Price> findVendorPrices(String vendorId) {
        return priceRepository.findAllByVendorId(vendorId);
    }

}
