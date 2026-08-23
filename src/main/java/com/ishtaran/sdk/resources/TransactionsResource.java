package com.ishtaran.sdk.resources;

import com.ishtaran.sdk.http.HttpRequest;
import com.ishtaran.sdk.http.HttpTransport;
import com.ishtaran.sdk.idempotency.IdempotencyKeyGenerator;
import com.ishtaran.sdk.model.dataplane.CreateTransactionResult;
import com.ishtaran.sdk.model.dataplane.ParticipantInput;
import com.ishtaran.sdk.model.dataplane.ReserveTransactionResult;
import com.ishtaran.sdk.model.dataplane.TransactionResponse;
import com.ishtaran.sdk.model.dataplane.TransactionStateResponse;
import com.ishtaran.sdk.model.enums.TransactionStatus;
import com.ishtaran.sdk.serialization.JsonCodec;
import com.ishtaran.sdk.util.Polling;

import java.math.BigDecimal;
import java.time.Duration;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

/** Data Plane — {@code Transactions} (7 real routes). */
public final class TransactionsResource extends ApiResourceSupport {

    public TransactionsResource(HttpTransport transport) {
        super(transport);
    }

    public CreateTransactionResult create(UUID organizationId, UUID applicationId, UUID workflowVersionId,
                                           UUID assetNetworkId, BigDecimal amount,
                                           List<ParticipantInput> participants, String idempotencyKey) {
        var key = IdempotencyKeyGenerator.resolve(idempotencyKey);
        var payload = new java.util.LinkedHashMap<String, Object>();
        payload.put("applicationId", applicationId);
        payload.put("workflowVersionId", workflowVersionId);
        payload.put("assetNetworkId", assetNetworkId);
        payload.put("amount", amount);
        payload.put("participants", participants);
        payload.put("idempotencyKey", key);
        var body = toJson(payload);
        return execute(HttpRequest.post("/v1/organizations/" + organizationId + "/transactions", body, true),
                CreateTransactionResult.class);
    }

    public TransactionResponse get(UUID transactionId) {
        return execute(HttpRequest.get("/v1/transactions/" + transactionId), TransactionResponse.class);
    }

    public TransactionStateResponse getState(UUID transactionId) {
        return execute(HttpRequest.get("/v1/transactions/" + transactionId + "/state"), TransactionStateResponse.class);
    }

    public ReserveTransactionResult reserve(UUID transactionId) {
        return execute(HttpRequest.post("/v1/transactions/" + transactionId + "/reserve", null, true),
                ReserveTransactionResult.class);
    }

    public void cancel(UUID transactionId, String reason) {
        var body = toJson(Map.of("reason", reason == null ? "" : reason));
        executeNoContent(HttpRequest.post("/v1/transactions/" + transactionId + "/cancel", body, false));
    }

    public void freeze(UUID transactionId, String reason) {
        var body = toJson(Map.of("reason", reason == null ? "" : reason));
        executeNoContent(HttpRequest.post("/v1/transactions/" + transactionId + "/freeze", body, false));
    }

    public void unfreeze(UUID transactionId) {
        executeNoContent(HttpRequest.post("/v1/transactions/" + transactionId + "/unfreeze", null, false));
    }

    private static final Set<TransactionStatus> TERMINAL_STATUSES = Set.of(
            TransactionStatus.SETTLED, TransactionStatus.REFUNDED, TransactionStatus.CANCELLED);

    /**
     * Safe polling, never infinite — always explicit {@code timeout}/{@code pollInterval} (see
     * SDK_CAPABILITY_SPEC.md §15). Terminates on Settled/Refunded/Cancelled — never on
     * Frozen/PartiallySettled/PartiallyRefunded, which can still change.
     */
    public TransactionResponse waitFor(UUID transactionId, Duration timeout, Duration pollInterval) {
        return Polling.until(() -> get(transactionId), r -> TERMINAL_STATUSES.contains(r.status()),
                timeout, pollInterval, "transactionId=" + transactionId);
    }

    private String toJson(Object value) {
        try {
            return JsonCodec.mapper().writeValueAsString(value);
        } catch (Exception e) {
            throw new IllegalStateException("Failed to serialize request body", e);
        }
    }
}
