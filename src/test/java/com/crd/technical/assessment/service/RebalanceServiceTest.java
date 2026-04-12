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

//@Slf4j
class RebalanceServiceTest {
    private static final ObjectMapper mapper = new ObjectMapper();
    JsonNode jsonNode;

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

    private static void assertRebalancingSecurities(Map<String, Security> result, String securityName, int expectedBuySell) {
        Security orclSecurity = result.get(securityName);
        assertNotNull(orclSecurity);
        assertEquals(expectedBuySell, orclSecurity.getShareBuySell());
        assertEquals(0, Math.abs(orclSecurity.getShareBuySell()) - Math.abs(orclSecurity.getTargetVarianceShares()));
    }
}