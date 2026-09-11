package com.ishtaran.sdk.resources;

import com.ishtaran.sdk.http.FakeHttpTransport;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;

class LedgerResourceTest {

    @Test
    void getBalance_parsesAvailablePendingReserved() {
        var fake = new FakeHttpTransport().enqueue(FakeHttpTransport.json(200, "{\"available\":100,\"pending\":0,\"reserved\":0}"));
        var resource = new LedgerResource(fake);

        var balance = resource.getBalance(UUID.randomUUID(), UUID.randomUUID());

        assertEquals(new BigDecimal("100"), balance.available());
    }

    /** G.2 (found 2026-09-11, SDK audit): payable/reservedForPayout/delivered were silently dropped. */
    @Test
    void getBalance_parsesPayableReservedForPayoutDelivered_g2() {
        var fake = new FakeHttpTransport().enqueue(FakeHttpTransport.json(200,
                "{\"available\":0,\"pending\":0,\"reserved\":0,\"payable\":178.38,\"reservedForPayout\":19.82,\"delivered\":178.38}"));
        var resource = new LedgerResource(fake);

        var balance = resource.getBalance(UUID.randomUUID(), UUID.randomUUID());

        assertEquals(new BigDecimal("178.38"), balance.payable());
        assertEquals(new BigDecimal("19.82"), balance.reservedForPayout());
        assertEquals(new BigDecimal("178.38"), balance.delivered());
    }

    @Test
    void getBalance_defaultsPayableReservedForPayoutDelivered_toZero_whenOmitted() {
        var fake = new FakeHttpTransport().enqueue(FakeHttpTransport.json(200, "{\"available\":100,\"pending\":0,\"reserved\":0}"));
        var resource = new LedgerResource(fake);

        var balance = resource.getBalance(UUID.randomUUID(), UUID.randomUUID());

        assertEquals(BigDecimal.ZERO, balance.payable());
        assertEquals(BigDecimal.ZERO, balance.reservedForPayout());
        assertEquals(BigDecimal.ZERO, balance.delivered());
    }
}
