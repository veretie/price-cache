package com.mits4u.example.priceCache.api.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.google.common.base.MoreObjects;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@ApiModel(value = "InstrumentPrice")
@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class InstrumentPrice implements Serializable {

    @ApiModelProperty(value = "instrument id", example = "AMZN")
    private String instrumentId;

    @ApiModelProperty(value = "vendor id", example = "BBG")
    private String vendorId;

    @ApiModelProperty(value = "instrument price", example = "0.95")
    private BigDecimal price;

    @ApiModelProperty(value = "price timestamp", example = "2020-06-13 12:08:03")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
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
