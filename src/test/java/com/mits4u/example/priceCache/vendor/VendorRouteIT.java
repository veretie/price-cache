package com.mits4u.example.priceCache.vendor;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.mits4u.example.priceCache.IntegrationTestHelper;
import com.mits4u.example.priceCache.TestStubs;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;

import static org.awaitility.Awaitility.await;
import static org.hamcrest.Matchers.is;

@SpringBootTest
@DirtiesContext
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class VendorRouteIT {

    @Autowired
    private IntegrationTestHelper testHelper;

    @Test
    void addPrice__JmsChannel__happyPath() throws JsonProcessingException {

        testHelper.sendVendorJmsMessage(TestStubs.stubApiPrice("J1", "BBG"));

        await().until(() -> testHelper.priceRecordedInDb("J1"), is(true));

    }

    @Test
    void addPrice__JmsChannel__invalidData() throws JsonProcessingException {

        testHelper.sendVendorJmsMessage(TestStubs.stubInvalidApiPrice("J2"));

        await().until(() -> testHelper.messageInDlq("J2", 5000), is(true));

    }

    @Test
    void addPrice__JmsChannel__invalidMessage() throws JsonProcessingException {

        testHelper.sendInvalidFormatMessage("J3");

        await().until(() -> testHelper.messageInDlq("J3", 5000), is(true));

    }

}
