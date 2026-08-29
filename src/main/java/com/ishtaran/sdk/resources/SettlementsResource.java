package com.ishtaran.sdk.resources;

import com.fasterxml.jackson.core.type.TypeReference;
import com.ishtaran.sdk.http.HttpRequest;
import com.ishtaran.sdk.http.HttpTransport;
import com.ishtaran.sdk.idempotency.IdempotencyKeyGenerator;
import com.ishtaran.sdk.model.dataplane.ExecuteSettlementResult;
import com.ishtaran.sdk.model.dataplane.SettlementResponse;
import com.ishtaran.sdk.model.dataplane.TransactionSettlementSummaryResponse;
import com.ishtaran.sdk.serialization.JsonCodec;

import java.math.BigDecimal;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/** Data Plane — {@code Settlement} (5 real routes; Refunds in {@link RefundsResource}). */
public final class SettlementsResource extends ApiResourceSupport {

    public SettlementsResource(HttpTransport transport) {
        super(transport);
    }

    /**
     * {@code amount} null = settle the full remaining reserved amount (unchanged default);
     * informed = settle exactly that amount (BL-STL-008, activated 2026-08-26) -- callable
     * repeatedly on the same Transaction until the remaining reserved balance reaches zero, each
     * call computing its own Platform Fee on its own gross slice.
     */
    public ExecuteSettlementResult executeSettlement(UUID transactionId, BigDecimal amount, String idempotencyKey) {
        var payload = new LinkedHashMap<String, Object>();
        payload.put("idempotencyKey", IdempotencyKeyGenerator.resolve(idempotencyKey));
        payload.put("amount", amount);
        var body = toJson(payload);
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
