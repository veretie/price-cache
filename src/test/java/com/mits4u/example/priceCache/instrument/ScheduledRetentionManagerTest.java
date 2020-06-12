package com.mits4u.example.priceCache.instrument;

import com.mits4u.example.priceCache.instrument.db.PriceDao;
import org.assertj.core.util.Lists;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

import static com.mits4u.example.priceCache.TestStubs.stubPrice;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class ScheduledRetentionManagerTest {

    @InjectMocks
    private ScheduledRetentionManager retentionManager;

    @Mock
    private PriceDao priceDao;

    private int daysValid = 30;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        retentionManager.setDaysValid(daysValid);
    }

    @Test
    void removeOutdatedRecords_emptyCollection() {

        var captor = ArgumentCaptor.forClass(LocalDateTime.class);
        when(priceDao.findPricesBefore(captor.capture())).thenReturn(Lists.emptyList());

        retentionManager.removeOutdatedRecords();

        LocalDateTime usedTimeToRemoveBefore = captor.getValue();
        LocalDateTime currentTimeToRemoveBefore = LocalDateTime.now().minus(daysValid, ChronoUnit.DAYS);
        var millisSinceRetentionRun = ChronoUnit.MILLIS.between(usedTimeToRemoveBefore, currentTimeToRemoveBefore);

        assertThat(millisSinceRetentionRun).isLessThanOrEqualTo(100);
        verify(priceDao, never()).delete(any());

    }

    @Test
    void removeOutdatedRecords() {

        var entity = stubPrice("P1", "HL");
        when(priceDao.findPricesBefore(any())).thenReturn(Lists.newArrayList(entity));

        retentionManager.removeOutdatedRecords();

        verify(priceDao).delete(entity);

    }

}