package com.mits4u.example.priceCache.instrument;

import com.mits4u.example.priceCache.instrument.db.PriceDao;
import lombok.Setter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

@Component
public class ScheduledRetentionManager {

    private Logger LOGGER = LoggerFactory.getLogger(ScheduledRetentionManager.class);

    @Setter
    @Value("${priceCache.retention.daysValid}")
    private int daysValid;

    @Autowired
    private PriceDao priceDao;

    @Scheduled(cron = "${priceCache.retention.cron}")
    @Transactional
    public void removeOutdatedRecords() {

        var oldestAllowed = LocalDateTime.now().minus(daysValid, ChronoUnit.DAYS);
        var oldRecords = priceDao.findPricesBefore(oldestAllowed);

        oldRecords.stream().forEach(price -> {
            priceDao.delete(price);
            LOGGER.info("deleted price={}", price);
        });

    }

}
