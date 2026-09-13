package com.ishtaran.sdk.resources;

import com.ishtaran.sdk.http.FakeHttpTransport;
import com.ishtaran.sdk.model.enums.DerivationScheme;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class WalletsResourceTest {

    // BR-TRF-008 (PROMPT 7.1) -- account-scoped registration.

    @Test
    void registerForAccount_postsToTheAccountScopedRoute_mapsTheWalletId() {
        UUID orgId = UUID.randomUUID();
        UUID accountId = UUID.randomUUID();
        UUID applicationId = UUID.randomUUID();
        UUID networkId = UUID.randomUUID();
        UUID walletId = UUID.randomUUID();
        var fake = new FakeHttpTransport().enqueue(FakeHttpTransport.json(201, "{\"walletId\":\"" + walletId + "\"}"));
        var resource = new WalletsResource(fake);

        var result = resource.registerForAccount(orgId, accountId, applicationId, networkId,
                DerivationScheme.TRON_BIP44_HARDENED_ACCOUNT, "xpub-account-owned...", "idem-1");

        assertEquals(walletId, result.walletId());
        assertTrue(fake.received().get(0).path().equals("/v1/organizations/" + orgId + "/accounts/" + accountId + "/wallets"));
    }

    @Test
    void registerForAccount_neverIncludesAccountIdInTheBody_itComesFromTheRoute() {
        UUID orgId = UUID.randomUUID();
        UUID accountId = UUID.randomUUID();
        UUID applicationId = UUID.randomUUID();
        UUID networkId = UUID.randomUUID();
        var fake = new FakeHttpTransport().enqueue(FakeHttpTransport.json(201, "{\"walletId\":\"" + UUID.randomUUID() + "\"}"));
        var resource = new WalletsResource(fake);

        resource.registerForAccount(orgId, accountId, applicationId, networkId,
                DerivationScheme.TRON_BIP44_HARDENED_ACCOUNT, "xpub-account-owned...", "idem-2");

        var sentBody = fake.received().get(0).body();
        assertTrue(sentBody.contains("\"applicationId\""));
        assertTrue(!sentBody.contains(accountId.toString()));
    }
}
