package com.mits4u.example.priceCache.instrument;

import com.mits4u.example.priceCache.IntegrationTestHelper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.jdbc.Sql;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.context.jdbc.Sql.ExecutionPhase.AFTER_TEST_METHOD;
import static org.springframework.test.context.jdbc.Sql.ExecutionPhase.BEFORE_TEST_METHOD;

@SpringBootTest
@DirtiesContext
@Sql(scripts = {"/retentionTestData.sql"}, executionPhase = BEFORE_TEST_METHOD)
@Sql(scripts = {"/teardownData.sql"}, executionPhase = AFTER_TEST_METHOD)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class ScheduledRetentionManagerIT {

    @Autowired
    private ScheduledRetentionManager scheduledRetentionManager;

    @Autowired
    private IntegrationTestHelper testHelper;

    @Test
    public void removeOutdatedRecords() {

        assertThat(testHelper.priceRecordedInDb("VALID")).isTrue();
        assertThat(testHelper.priceRecordedInDb("OLD")).isTrue();

        scheduledRetentionManager.removeOutdatedRecords();

        assertThat(testHelper.priceRecordedInDb("VALID")).isTrue();
        assertThat(testHelper.priceRecordedInDb("OLD")).isFalse();

    }


}