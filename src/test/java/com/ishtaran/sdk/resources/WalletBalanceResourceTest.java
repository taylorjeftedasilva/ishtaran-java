package com.ishtaran.sdk.resources;

import com.ishtaran.sdk.http.FakeHttpTransport;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class WalletBalanceResourceTest {

    @Test
    void getBalance_getsTheCachedSnapshot_parsesFreshnessFields() {
        UUID accountId = UUID.randomUUID();
        UUID environmentId = UUID.randomUUID();
        UUID assetNetworkId = UUID.randomUUID();
        String body = """
                {"accountId":"%s","assetNetworkId":"%s","address":"TWallet1","balance":100.5,
                 "observedAt":"2026-09-11T12:00:00Z","blockReference":"block-1","source":"sandbox",
                 "stale":false,"nextRefreshAllowedAt":null,"refreshSuppressed":false,"refreshFailureReason":null}
                """.formatted(accountId, assetNetworkId);
        var fake = new FakeHttpTransport().enqueue(FakeHttpTransport.json(200, body));
        var resource = new WalletBalanceResource(fake);

        var result = resource.getBalance(accountId, environmentId, assetNetworkId);

        assertEquals(new BigDecimal("100.5"), result.balance());
        assertEquals("TWallet1", result.address());
        assertFalse(result.stale());
        assertTrue(fake.received().get(0).path().equals(
                "/v1/accounts/" + accountId + "/wallet-balances?environmentId=" + environmentId + "&assetNetworkId=" + assetNetworkId));
    }

    @Test
    void getBalance_neverObservedYet_reportsZeroBalanceNeverALie() {
        UUID accountId = UUID.randomUUID();
        String body = """
                {"accountId":"%s","assetNetworkId":"%s","address":"TWallet1","balance":0,
                 "observedAt":null,"blockReference":null,"source":null,"stale":true,
                 "nextRefreshAllowedAt":null,"refreshSuppressed":false,"refreshFailureReason":null}
                """.formatted(accountId, UUID.randomUUID());
        var fake = new FakeHttpTransport().enqueue(FakeHttpTransport.json(200, body));
        var resource = new WalletBalanceResource(fake);

        var result = resource.getBalance(accountId, UUID.randomUUID(), UUID.randomUUID());

        assertNull(result.observedAt());
        assertEquals(0, BigDecimal.ZERO.compareTo(result.balance()));
        assertTrue(result.stale());
    }

    @Test
    void refreshBalance_postsWithNoBody_neverTellsThePlatformWhatTheBalanceShouldBe() {
        UUID accountId = UUID.randomUUID();
        String body = """
                {"accountId":"%s","assetNetworkId":"%s","address":"TWallet1","balance":50,
                 "observedAt":"2026-09-11T12:00:00Z","blockReference":null,"source":"trongrid",
                 "stale":false,"nextRefreshAllowedAt":"2026-09-11T12:00:30Z","refreshSuppressed":false,"refreshFailureReason":null}
                """.formatted(accountId, UUID.randomUUID());
        var fake = new FakeHttpTransport().enqueue(FakeHttpTransport.json(200, body));
        var resource = new WalletBalanceResource(fake);

        var result = resource.refreshBalance(accountId, UUID.randomUUID(), UUID.randomUUID());

        assertEquals("trongrid", result.source());
        assertNull(fake.received().get(0).body());
    }

    @Test
    void getAssetBalances_aggregatesAcrossNetworks_groupedByAsset() {
        UUID assetId = UUID.randomUUID();
        UUID tronNetworkId = UUID.randomUUID();
        UUID ethNetworkId = UUID.randomUUID();
        String body = """
                [{"assetId":"%s","assetSymbol":"USDT","aggregateBalance":150,
                  "networkBalances":[
                    {"assetNetworkId":"%s","networkCode":"tron","balance":100,"observedAt":null,"stale":false},
                    {"assetNetworkId":"%s","networkCode":"ethereum","balance":50,"observedAt":null,"stale":false}
                  ]}]
                """.formatted(assetId, tronNetworkId, ethNetworkId);
        var fake = new FakeHttpTransport().enqueue(FakeHttpTransport.json(200, body));
        var resource = new WalletBalanceResource(fake);

        var results = resource.getAssetBalances(UUID.randomUUID(), UUID.randomUUID(), List.of(tronNetworkId, ethNetworkId));

        assertEquals(1, results.size());
        assertEquals("USDT", results.get(0).assetSymbol());
        assertEquals(new BigDecimal("150"), results.get(0).aggregateBalance());
        assertEquals(2, results.get(0).networkBalances().size());
        assertEquals("tron", results.get(0).networkBalances().get(0).networkCode());
    }
}
