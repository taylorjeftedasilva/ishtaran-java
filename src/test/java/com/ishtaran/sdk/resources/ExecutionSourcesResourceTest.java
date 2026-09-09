package com.ishtaran.sdk.resources;

import com.ishtaran.sdk.http.FakeHttpTransport;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ExecutionSourcesResourceTest {

    @Test
    void register_postsTheDerivationReferenceAndAddress_mapsTheCreatedId() {
        UUID organizationId = UUID.randomUUID();
        UUID environmentId = UUID.randomUUID();
        UUID assetNetworkId = UUID.randomUUID();
        UUID walletId = UUID.randomUUID();
        UUID executionSourceId = UUID.randomUUID();

        var fake = new FakeHttpTransport().enqueue(FakeHttpTransport.json(201, "{\"executionSourceId\":\"" + executionSourceId + "\"}"));
        var resource = new ExecutionSourcesResource(fake);

        var result = resource.register(organizationId, environmentId, assetNetworkId, walletId, 42L, "Txxx");

        assertEquals(executionSourceId, result.executionSourceId());
        assertEquals("POST", fake.received().get(0).method().name());
        assertTrue(fake.received().get(0).path().equals("/v1/organizations/" + organizationId + "/execution-sources"));
        assertTrue(fake.received().get(0).body().contains("\"derivationReference\":42"));
    }

    @Test
    void syncResourceStake_postsTheDeclaredCapacityToTheResourceStakeRoute() {
        UUID organizationId = UUID.randomUUID();
        UUID executionSourceId = UUID.randomUUID();

        var fake = new FakeHttpTransport().enqueue(FakeHttpTransport.json(204, ""));
        var resource = new ExecutionSourcesResource(fake);

        resource.syncResourceStake(organizationId, executionSourceId, new BigDecimal("100"), new BigDecimal("20000"), new BigDecimal("500"));

        assertEquals("POST", fake.received().get(0).method().name());
        assertEquals("/v1/organizations/" + organizationId + "/execution-sources/" + executionSourceId + "/resource-stake",
                fake.received().get(0).path());
        assertTrue(fake.received().get(0).body().contains("\"availableNativeAmount\":100"));
        assertTrue(fake.received().get(0).body().contains("\"availableEnergy\":20000"));
        assertTrue(fake.received().get(0).body().contains("\"availableBandwidth\":500"));
    }
}
