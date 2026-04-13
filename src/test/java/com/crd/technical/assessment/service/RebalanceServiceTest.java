package com.crd.technical.assessment.service;

import com.crd.technical.assessment.model.RebalanceRequest;
import com.crd.technical.assessment.model.Security;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class RebalanceServiceTest {
    private static final ObjectMapper mapper = new ObjectMapper();
    private JsonNode jsonNode;

    @BeforeEach
    void setUp() throws URISyntaxException, IOException {
        URL resource = RebalanceServiceTest.class
                .getClassLoader()
                .getResource("securitiesAllocation.json");

        assertNotNull(resource);
        File file = new File(resource.toURI());

        jsonNode = mapper.readValue(file, JsonNode.class);

    }

    @Test
    void testRebalanceHappyPathScenario() throws Exception {

        RebalanceService service = new RebalanceService();
        RebalanceRequest request = mapper.readValue(jsonNode.get("happyPathScenario").traverse(), RebalanceRequest.class);

        var result = service.rebalance(
                request.getTotalAsset(),
                request.getSecurities()
        );
        assertRebalancingSecurities(result, "IBM", 67);
        assertRebalancingSecurities(result, "ORCL", -45);
        assertRebalancingSecurities(result, "MSFT", 0);
        assertRebalancingSecurities(result, "AAPL", 0);
        assertRebalancingSecurities(result, "HD", 0);

    }

    @Test
    void testAlreadyBalancedScenario() throws Exception {

        RebalanceService service = new RebalanceService();
        RebalanceRequest request = mapper.readValue(jsonNode.get("alreadyBalancedScenario").traverse(), RebalanceRequest.class);

        var result = service.rebalance(
                request.getTotalAsset(),
                request.getSecurities()
        );
        assertRebalancingSecurities(result, "IBM", 0);
        assertRebalancingSecurities(result, "ORCL", 0);
        assertRebalancingSecurities(result, "MSFT", 0);
        assertRebalancingSecurities(result, "AAPL", 0);
        assertRebalancingSecurities(result, "HD", 0);

    }

    @Test
    void testOnlyBuyScenario() throws Exception {

        RebalanceService service = new RebalanceService();
        RebalanceRequest request = mapper.readValue(jsonNode.get("onlyBuyScenario").traverse(), RebalanceRequest.class);

        var result = service.rebalance(
                request.getTotalAsset(),
                request.getSecurities()
        );
        assertRebalancingSecurities(result, "IBM", 100);
        assertRebalancingSecurities(result, "ORCL", 23);
        assertRebalancingSecurities(result, "MSFT", 111);
        assertRebalancingSecurities(result, "AAPL", 44);

    }

    @Test
    void testOnlySellScenario() throws Exception {

        RebalanceService service = new RebalanceService();
        RebalanceRequest request = mapper.readValue(jsonNode.get("onlySellScenario").traverse(), RebalanceRequest.class);

        var result = service.rebalance(
                request.getTotalAsset(),
                request.getSecurities()
        );
        assertRebalancingSecurities(result, "IBM", -133);
        assertRebalancingSecurities(result, "ORCL", -45);
        assertRebalancingSecurities(result, "MSFT", -222);

    }

    @Test
    void testInvalidPercentageScenario() throws Exception {
        RebalanceService service = new RebalanceService();
        RebalanceRequest request = mapper.readValue(
                jsonNode.get("invalidPercentageScenario").traverse(),
                RebalanceRequest.class
        );

        Exception ex = assertThrows(IllegalArgumentException.class, () -> {
            service.rebalance(request.getTotalAsset(), request.getSecurities());
        });

        assertEquals("Total target percentage must be 100", ex.getMessage());
    }

    @Test
    void testMissingPriceScenario() throws Exception {
        RebalanceRequest request = mapper.readValue(
                jsonNode.get("missingPriceScenario").traverse(),
                RebalanceRequest.class
        );

        Exception ex = assertThrows(IllegalArgumentException.class, () -> {
            new RebalanceService().rebalance(request.getTotalAsset(), request.getSecurities());
        });

        assertEquals("Price must be greater than zero", ex.getMessage());
    }

    @Test
    void testNegativeValueScenario() throws Exception {
        RebalanceRequest request = mapper.readValue(
                jsonNode.get("negativeValueScenario").traverse(),
                RebalanceRequest.class
        );

        Exception ex = assertThrows(IllegalArgumentException.class, () -> {
            new RebalanceService().rebalance(request.getTotalAsset(), request.getSecurities());
        });

        assertEquals("Percentage cannot be negative", ex.getMessage());
    }

    @Test
    void testZeroAssetScenario() throws Exception {
        RebalanceRequest request = mapper.readValue(
                jsonNode.get("zeroAssetScenario").traverse(),
                RebalanceRequest.class
        );

        Exception ex = assertThrows(IllegalArgumentException.class, () -> {
            new RebalanceService().rebalance(request.getTotalAsset(), request.getSecurities());
        });

        assertEquals("Total asset must be greater than zero", ex.getMessage());
    }

    @Test
    void testEmptyPortfolioScenario() throws Exception {
        RebalanceRequest request = mapper.readValue(
                jsonNode.get("emptyPortfolioScenario").traverse(),
                RebalanceRequest.class
        );

        Exception ex = assertThrows(IllegalArgumentException.class, () -> {
            new RebalanceService().rebalance(request.getTotalAsset(), request.getSecurities());
        });

        assertEquals("Portfolio cannot be empty", ex.getMessage());
    }

    private static void assertRebalancingSecurities(Map<String, Security> result, String securityName, int expectedBuySell) {
        Security orclSecurity = result.get(securityName);
        assertNotNull(orclSecurity);
        assertEquals(expectedBuySell, orclSecurity.getShareBuySell());
        assertEquals(0, Math.abs(orclSecurity.getShareBuySell()) - Math.abs(orclSecurity.getTargetVarianceShares()));
    }
}