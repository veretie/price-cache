package com.mits4u.example.priceCache;

import com.mits4u.example.priceCache.api.model.InstrumentPrice;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.jdbc.Sql;
import org.assertj.core.groups.Tuple;

import java.math.BigDecimal;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.Collection;

import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;
import static org.hamcrest.Matchers.*;
import static org.springframework.test.context.jdbc.Sql.ExecutionPhase.AFTER_TEST_METHOD;
import static org.springframework.test.context.jdbc.Sql.ExecutionPhase.BEFORE_TEST_METHOD;

@SpringBootTest(classes = Application.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@DirtiesContext
@Sql(scripts = {"/setupData.sql"}, executionPhase = BEFORE_TEST_METHOD)
@Sql(scripts = {"/teardownData.sql"}, executionPhase = AFTER_TEST_METHOD)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class ApiControllerIT {

    @Value("${local.server.port}")
    private int port;

    @Autowired
    private IntegrationTestHelper testHelper;

    private String baseUrl;
    private TestRestTemplate template;

    @BeforeEach
    public void setUp() {
        baseUrl = "http://localhost:" + port + "/v1";
        template = new TestRestTemplate();
    }

    @Test
    void getPrices__vendor__pricesFound() throws URISyntaxException {

        var uri = new URI(baseUrl + "/cache/prices/vendor/BBG");
        var response = template.getForEntity(uri, InstrumentPrice[].class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

        var prices = Arrays.asList(response.getBody());
        assertThat(prices)
                .extracting("instrumentId", "price", "vendorId")
                .containsExactlyInAnyOrder(
                        Tuple.tuple("AMZN", new BigDecimal("2.10"), "BBG"),
                        Tuple.tuple("AAPL", new BigDecimal("2.20"), "BBG")
                );

    }

    @Test
    void getPrices__vendor__noPrices() throws URISyntaxException {

        var uri = new URI(baseUrl + "/cache/prices/vendor/unknown");
        var response = template.getForEntity(uri, Collection.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

        Collection<InstrumentPrice> prices = response.getBody();
        Assertions.assertThat(prices).isEmpty();

    }

    @Test
    void getPrices__instrument__pricesFound() throws URISyntaxException {

        var uri = new URI(baseUrl + "/cache/prices/instrument/AMZN");
        var response = template.getForEntity(uri, InstrumentPrice[].class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

        var prices = Arrays.asList(response.getBody());
        assertThat(prices)
                .extracting("instrumentId", "price", "vendorId")
                .containsExactlyInAnyOrder(
                        Tuple.tuple("AMZN", new BigDecimal("1.10"), "HL"),
                        Tuple.tuple("AMZN", new BigDecimal("2.10"), "BBG")
                );

    }

    @Test
    void getPrices__instrument__noPrices() throws URISyntaxException {

        var uri = new URI(baseUrl + "/cache/prices/instrument/unknown");
        var response = template.getForEntity(uri, Collection.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

        Collection<InstrumentPrice> prices = response.getBody();
        Assertions.assertThat(prices).isEmpty();

    }

    @Test
    void addPrice__RestChannel__happyPath() throws URISyntaxException {

        var uri = new URI(baseUrl + "/vendor/prices");
        var response = template.postForEntity(uri, TestStubs.stubApiPrice("R1", "BBG"), String.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        await().until(() -> testHelper.priceRecordedInDb("R1"), is(true));

    }

    @Test
    void addPrice__RestChannel__invalidRequest() throws URISyntaxException, InterruptedException {

        var uri = new URI(baseUrl + "/vendor/prices");
        var response = template.postForEntity(uri, TestStubs.stubInvalidApiPrice("R2"), String.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);

    }

}
