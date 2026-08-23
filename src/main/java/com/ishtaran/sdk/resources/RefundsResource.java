package com.ishtaran.sdk.resources;

import com.fasterxml.jackson.core.type.TypeReference;
import com.ishtaran.sdk.http.HttpRequest;
import com.ishtaran.sdk.http.HttpTransport;
import com.ishtaran.sdk.idempotency.IdempotencyKeyGenerator;
import com.ishtaran.sdk.model.dataplane.ExecuteRefundResult;
import com.ishtaran.sdk.model.dataplane.RefundResponse;
import com.ishtaran.sdk.serialization.JsonCodec;

import java.math.BigDecimal;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.UUID;

/** Data Plane — {@code Refunds} (3 real routes, under the same real {@code Settlement} module). */
public final class RefundsResource extends ApiResourceSupport {

    public RefundsResource(HttpTransport transport) {
        super(transport);
    }

    /** {@code amount} null = full refund. */
    public ExecuteRefundResult executeRefund(UUID transactionId, BigDecimal amount, String reason, String idempotencyKey) {
        var payload = new LinkedHashMap<String, Object>();
        payload.put("amount", amount);
        payload.put("reason", reason);
        payload.put("idempotencyKey", IdempotencyKeyGenerator.resolve(idempotencyKey));
        var body = toJson(payload);
        return execute(HttpRequest.post("/v1/transactions/" + transactionId + "/refunds", body, true),
                ExecuteRefundResult.class);
    }

    public List<RefundResponse> listByTransaction(UUID transactionId) {
        return execute(HttpRequest.get("/v1/transactions/" + transactionId + "/refunds"),
                new TypeReference<List<RefundResponse>>() {
                });
    }

    public RefundResponse get(UUID refundId) {
        return execute(HttpRequest.get("/v1/refunds/" + refundId), RefundResponse.class);
    }

    private String toJson(Object value) {
        try {
            return JsonCodec.mapper().writeValueAsString(value);
        } catch (Exception e) {
            throw new IllegalStateException("Failed to serialize request body", e);
        }
    }
}
