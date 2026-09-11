package com.ishtaran.sdk.resources;

import com.ishtaran.sdk.http.FakeHttpTransport;
import com.ishtaran.sdk.model.dataplane.ExecutionResponse;
import com.ishtaran.sdk.model.enums.ExecutionStatus;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/** PROMPT 5 section 9 (G.7) -- network-free integration (FakeHttpTransport) for the new discoverability route. */
class TransactionsResourceSearchExecutionsTest {

    @Test
    void searchExecutions_scopesByOrganizationId_andForwardsQueryParams() {
        UUID orgId = UUID.randomUUID();
        UUID txId = UUID.randomUUID();
        UUID settlementId = UUID.randomUUID();
        var fake = new FakeHttpTransport().enqueue(FakeHttpTransport.json(200, "[]"));
        var resource = new TransactionsResource(fake);

        resource.searchExecutions(orgId, ExecutionStatus.OVERDUE, txId, settlementId, null, null, 10, 25);

        assertEquals("GET", fake.received().get(0).method().name());
        var path = fake.received().get(0).path();
        assertTrue(path.startsWith("/v1/organizations/" + orgId + "/executions?"));
        assertTrue(path.contains("status=5"));
        assertTrue(path.contains("transactionId=" + txId));
        assertTrue(path.contains("settlementId=" + settlementId));
        assertTrue(path.contains("skip=10"));
        assertTrue(path.contains("take=25"));
    }

    @Test
    void searchExecutions_mapsNullableExecutedAtAndSettlementId() {
        UUID orgId = UUID.randomUUID();
        UUID exId = UUID.randomUUID();
        UUID txId = UUID.randomUUID();
        String body = """
                [
                  {
                    "executionId": "%s", "transactionId": "%s", "organizationId": "%s",
                    "status": 5, "preparedAt": "2026-08-01T12:00:00Z", "gracePeriodExpiresAt": "2026-08-01T13:00:00Z",
                    "executedAt": null, "settlementId": null
                  }
                ]
                """.formatted(exId, txId, orgId);
        var fake = new FakeHttpTransport().enqueue(FakeHttpTransport.json(200, body));
        var resource = new TransactionsResource(fake);

        List<ExecutionResponse> executions = resource.searchExecutions(orgId, null, null, null, null, null, null, null);

        assertEquals(1, executions.size());
        assertEquals(exId, executions.get(0).executionId());
        assertEquals("OVERDUE", executions.get(0).status().name());
        assertNull(executions.get(0).executedAt());
        assertNull(executions.get(0).settlementId());
    }
}
