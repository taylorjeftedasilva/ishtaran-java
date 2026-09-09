package com.ishtaran.sdk.resources;

import com.ishtaran.sdk.error.ConflictError;
import com.ishtaran.sdk.http.FakeHttpTransport;
import com.ishtaran.sdk.model.enums.NetworkResourceSource;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class NetworkCostPayerAccountsResourceTest {

    @Test
    void register_postsAssetNetworkIdAndAccountId_mapsTheCreatedId() {
        UUID organizationId = UUID.randomUUID();
        UUID networkCostPayerAccountId = UUID.randomUUID();

        var fake = new FakeHttpTransport().enqueue(FakeHttpTransport.json(201, "{\"networkCostPayerAccountId\":\"" + networkCostPayerAccountId + "\"}"));
        var resource = new NetworkCostPayerAccountsResource(fake);

        var result = resource.register(organizationId, UUID.randomUUID(), UUID.randomUUID());

        assertEquals(networkCostPayerAccountId, result.networkCostPayerAccountId());
    }

    @Test
    void aCrossTenantAccount_isRejected_mappedToA4xxError_neverARaw500() {
        var fake = new FakeHttpTransport().enqueue(FakeHttpTransport.json(409,
                "{\"status\":409,\"detail\":\"Account does not belong to this Organization\",\"code\":\"CONFLICT\"}"));
        var resource = new NetworkCostPayerAccountsResource(fake);

        assertThrows(ConflictError.class, () -> resource.register(UUID.randomUUID(), UUID.randomUUID(), UUID.randomUUID()));
    }

    @Test
    void updateResourcePreference_patchesTheResourcePreferenceAndFallbackFlag() {
        UUID organizationId = UUID.randomUUID();
        UUID assetNetworkId = UUID.randomUUID();

        var fake = new FakeHttpTransport().enqueue(FakeHttpTransport.json(204, ""));
        var resource = new NetworkCostPayerAccountsResource(fake);

        resource.updateResourcePreference(organizationId, assetNetworkId, NetworkResourceSource.SELF, true);

        assertEquals("PATCH", fake.received().get(0).method().name());
        assertEquals("/v1/organizations/" + organizationId + "/network-cost-payer-accounts/" + assetNetworkId + "/resource-preference",
                fake.received().get(0).path());
        assertTrue(fake.received().get(0).body().contains("\"resourcePreference\":1"));
        assertTrue(fake.received().get(0).body().contains("\"allowFallbackToIshtaranResources\":true"));
    }
}
