package com.mits4u.example.priceCache.api;

import com.mits4u.example.priceCache.vendor.GenericVendorJmsAdaptor;
import com.mits4u.example.priceCache.api.model.InstrumentPrice;
import com.mits4u.example.priceCache.client.CacheService;
import com.sun.istack.NotNull;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.NonNull;
import org.springframework.web.bind.annotation.*;

import java.util.Collection;

@Api(value = "Instrument price caching service", produces = "application/json")
@RestController
public class ApiController {

    @Autowired
    private GenericVendorJmsAdaptor genericVendorJmsAdaptor;

    @Autowired
    private CacheService cacheService;

    @ApiOperation(value = "Add vendor price")
    @PostMapping(value = "/v1/vendor/prices", consumes = "application/json")
    public void addVendorPrice(@NonNull @RequestBody @NotNull InstrumentPrice instrumentPrice) {

        genericVendorJmsAdaptor.validateAndSend(instrumentPrice);

    }

    @ApiOperation(value = "Get prices from specific vendor")
    @GetMapping(value = "/v1/cache/prices/vendor/{vendorId}", produces = "application/json")
    public Collection<InstrumentPrice> getVendorPrices(
            @ApiParam(value = "vendor id", required = true) @PathVariable("vendorId") String vendorId) {

        return cacheService.getPricesFromVendor(vendorId);

    }

    @ApiOperation(value = "Get prices for specific instrumentId")
    @GetMapping(value = "/v1/cache/prices/instrument/{instrumentId}", produces = "application/json")
    public Collection<InstrumentPrice> getInstrumentPrices(
            @ApiParam(value = "instrument id", required = true) @PathVariable("instrumentId") String instrumentId) {

        return cacheService.getPricesForInstrument(instrumentId);

    }


}