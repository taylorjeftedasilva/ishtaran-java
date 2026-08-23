package com.ishtaran.sdk.resources;

import com.fasterxml.jackson.core.type.TypeReference;
import com.ishtaran.sdk.http.HttpRequest;
import com.ishtaran.sdk.http.HttpTransport;
import com.ishtaran.sdk.idempotency.IdempotencyKeyGenerator;
import com.ishtaran.sdk.model.dataplane.ExecuteSettlementResult;
import com.ishtaran.sdk.model.dataplane.SettlementResponse;
import com.ishtaran.sdk.model.dataplane.TransactionSettlementSummaryResponse;
import com.ishtaran.sdk.serialization.JsonCodec;

import java.util.List;
import java.util.Map;
import java.util.UUID;

/** Data Plane — {@code Settlement} (5 real routes; Refunds in {@link RefundsResource}). */
public final class SettlementsResource extends ApiResourceSupport {

    public SettlementsResource(HttpTransport transport) {
        super(transport);
    }

    public ExecuteSettlementResult executeSettlement(UUID transactionId, String idempotencyKey) {
        var body = toJson(Map.of("idempotencyKey", IdempotencyKeyGenerator.resolve(idempotencyKey)));
        return execute(HttpRequest.post("/v1/transactions/" + transactionId + "/settlements", body, true),
                ExecuteSettlementResult.class);
    }

    public List<SettlementResponse> listByTransaction(UUID transactionId) {
        return execute(HttpRequest.get("/v1/transactions/" + transactionId + "/settlements"),
                new TypeReference<List<SettlementResponse>>() {
                });
    }

    public SettlementResponse get(UUID settlementId) {
        return execute(HttpRequest.get("/v1/settlements/" + settlementId), SettlementResponse.class);
    }

    public TransactionSettlementSummaryResponse getSummary(UUID transactionId) {
        return execute(HttpRequest.get("/v1/transactions/" + transactionId + "/settlement-summary"),
                TransactionSettlementSummaryResponse.class);
    }

    public void releaseRetainedSplit(UUID settlementId, UUID allocationId, String idempotencyKey) {
        var body = toJson(Map.of("idempotencyKey", IdempotencyKeyGenerator.resolve(idempotencyKey)));
        executeNoContent(HttpRequest.post(
                "/v1/settlements/" + settlementId + "/split-allocations/" + allocationId + "/release", body, true));
    }

    private String toJson(Object value) {
        try {
            return JsonCodec.mapper().writeValueAsString(value);
        } catch (Exception e) {
            throw new IllegalStateException("Failed to serialize request body", e);
        }
    }
}
