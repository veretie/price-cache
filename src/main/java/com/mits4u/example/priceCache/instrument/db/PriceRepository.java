package com.mits4u.example.priceCache.instrument.db;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.Collection;

public interface PriceRepository extends JpaRepository<Price, Long> {

    @Query("select a from INSTRUMENT_PRICES a where a.priceTimestamp < :oldestAllowed")
    Collection<Price> findAllWithCreationDateTimeBefore(@Param("oldestAllowed") LocalDateTime timestamp);

    Collection<Price> findAllByInstrumentId(String instrumentId);

    Collection<Price> findAllByVendorId(String vendorId);

}
