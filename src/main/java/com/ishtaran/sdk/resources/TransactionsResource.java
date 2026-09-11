package com.ishtaran.sdk.resources;

import com.fasterxml.jackson.core.type.TypeReference;
import com.ishtaran.sdk.http.HttpRequest;
import com.ishtaran.sdk.http.HttpTransport;
import com.ishtaran.sdk.idempotency.IdempotencyKeyGenerator;
import com.ishtaran.sdk.model.dataplane.CreateTransactionResult;
import com.ishtaran.sdk.model.dataplane.ExecutionResponse;
import com.ishtaran.sdk.model.dataplane.ParticipantInput;
import com.ishtaran.sdk.model.dataplane.ReserveTransactionResult;
import com.ishtaran.sdk.model.dataplane.TransactionResponse;
import com.ishtaran.sdk.model.dataplane.TransactionStateResponse;
import com.ishtaran.sdk.model.enums.ExecutionStatus;
import com.ishtaran.sdk.model.enums.TransactionStatus;
import com.ishtaran.sdk.pagination.PageIterator;
import com.ishtaran.sdk.serialization.JsonCodec;
import com.ishtaran.sdk.util.Polling;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.OffsetDateTime;
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

    /**
     * PROMPT 5 section 9 (G.7) -- discoverability for outstanding/overdue Executions. The safety
     * rule that makes an Organization settlement-restricted on an overdue Execution stays -- this
     * closes the operational hole of finding which Execution caused it. Scoped by
     * {@code organizationId}, same authorization model as every other
     * {@code /v1/organizations/{organizationId}/...} route -- never cross-tenant.
     */
    public List<ExecutionResponse> searchExecutions(UUID organizationId, ExecutionStatus status, UUID transactionId,
                                                      UUID settlementId, OffsetDateTime from, OffsetDateTime to,
                                                      Integer skip, Integer take) {
        var query = new StringBuilder("/v1/organizations/" + organizationId + "/executions?");
        if (status != null) {
            query.append("status=").append(status.rawValue()).append('&');
        }
        if (transactionId != null) {
            query.append("transactionId=").append(transactionId).append('&');
        }
        if (settlementId != null) {
            query.append("settlementId=").append(settlementId).append('&');
        }
        if (from != null) {
            query.append("from=").append(from).append('&');
        }
        if (to != null) {
            query.append("to=").append(to).append('&');
        }
        if (skip != null) {
            query.append("skip=").append(skip).append('&');
        }
        if (take != null) {
            query.append("take=").append(take).append('&');
        }
        return execute(HttpRequest.get(query.toString()), new TypeReference<List<ExecutionResponse>>() {
        });
    }

    /**
     * Lazy iterator (fetches the next page on demand, never loads everything at once) -- see
     * SDK_CAPABILITY_SPEC.md section 12.7.
     */
    public PageIterator<ExecutionResponse> searchExecutionsAll(UUID organizationId, ExecutionStatus status, UUID transactionId,
                                                                 UUID settlementId, OffsetDateTime from, OffsetDateTime to, int pageSize) {
        return new PageIterator<>(pageSize, (skip, take) ->
                searchExecutions(organizationId, status, transactionId, settlementId, from, to, skip, take));
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
