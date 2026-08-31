package com.ishtaran.sdk.resources;

import com.ishtaran.sdk.error.TimeoutError;
import com.ishtaran.sdk.http.FakeHttpTransport;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class WithdrawalsWaitForTest {

    @Test
    void waitFor_pollsUntilTerminalStatus() {
        UUID id = UUID.randomUUID();
        var fake = new FakeHttpTransport()
                .enqueue(FakeHttpTransport.json(200, withdrawalJson(id, 2))) // PendingApproval
                .enqueue(FakeHttpTransport.json(200, withdrawalJson(id, 8))); // Completed
        var resource = new WithdrawalsResource(fake);

        var result = resource.waitFor(id, Duration.ofSeconds(5), Duration.ofMillis(1));

        assertEquals(8, result.status().rawValue());
        assertEquals(2, fake.requestCount());
    }

    @Test
    void waitFor_neverResolves_throwsTimeoutError() {
        UUID id = UUID.randomUUID();
        var fake = new FakeHttpTransport().respondAlways(req -> FakeHttpTransport.json(200, withdrawalJson(id, 2)));
        var resource = new WithdrawalsResource(fake);

        assertThrows(TimeoutError.class, () -> resource.waitFor(id, Duration.ofMillis(20), Duration.ofMillis(5)));
    }

    private static String withdrawalJson(UUID id, int status) {
        return """
                {"withdrawalId":"%s","organizationId":"%s","environmentId":"%s","accountId":"%s","withdrawalDestinationId":"%s",
                 "assetNetworkId":"%s","amount":100,"estimatedNetworkFee":null,"estimatedRecipientAmount":100,
                 "finalNetworkFee":null,"finalRecipientAmount":null,"status":%d,"entryGroupId":null,
                 "technicalReference":null,"signingRequestId":null,"networkExecutionCost":null,
                 "networkExecutionCostStatus":null,"createdAt":"2026-08-17T12:00:00Z"}
                """.formatted(id, UUID.randomUUID(), UUID.randomUUID(), UUID.randomUUID(), UUID.randomUUID(), UUID.randomUUID(), status);
    }
}
