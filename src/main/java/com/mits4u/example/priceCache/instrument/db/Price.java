package com.mits4u.example.priceCache.instrument.db;

import com.google.common.base.MoreObjects;
import lombok.*;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity(name = "INSTRUMENT_PRICES")
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
public class Price {

    @Id
    @GeneratedValue
    private long id;

    private String instrumentId;

    private String vendorId;

    private BigDecimal price;

    private LocalDateTime priceTimestamp;

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("instrumentId", instrumentId)
                .add("vendorId", vendorId)
                .add("price", price)
                .add("priceTimestamp", priceTimestamp)
                .toString();

    }

}
