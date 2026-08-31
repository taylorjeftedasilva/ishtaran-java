package com.ishtaran.sdk.resources;

import com.ishtaran.sdk.http.HttpRequest;
import com.ishtaran.sdk.http.HttpTransport;
import com.ishtaran.sdk.idempotency.IdempotencyKeyGenerator;
import com.ishtaran.sdk.model.dataplane.CreatePayoutBatchResult;
import com.ishtaran.sdk.model.dataplane.PayableSummaryResponse;
import com.ishtaran.sdk.model.dataplane.PayoutBatchResponse;
import com.ishtaran.sdk.serialization.JsonCodec;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.UUID;

/**
 * Data Plane — {@code Payout} (SPEC-024/SPEC-025). Under {@code PayoutPolicy.IMMEDIATE} a
 * beneficiary's Payable is settled the same moment as the Settlement itself (no PayoutBatch
 * involved); under a batched policy the beneficiary only has an economic Receivable
 * ({@link #getPayableSummary}) until a PayoutBatch actually executes. This SDK slice only ever
 * creates batches with {@code trigger = MANUAL} (the public route accepts no other trigger yet).
 */
public final class PayoutResource extends ApiResourceSupport {

    public PayoutResource(HttpTransport transport) {
        super(transport);
    }

    public PayableSummaryResponse getPayableSummary(UUID accountId, UUID assetNetworkId) {
        return execute(HttpRequest.get("/v1/accounts/" + accountId + "/payable-summary?assetNetworkId=" + assetNetworkId),
                PayableSummaryResponse.class);
    }

    /** {@code payoutBatchId} in the result is {@code null} when there were no eligible candidates (204 No Content, a legitimate no-op). */
    public CreatePayoutBatchResult createBatch(UUID organizationId, UUID environmentId, UUID assetNetworkId,
                                                List<UUID> explicitOwnerIds, String idempotencyKey) {
        var key = IdempotencyKeyGenerator.resolve(idempotencyKey);
        var payload = new LinkedHashMap<String, Object>();
        payload.put("environmentId", environmentId);
        payload.put("assetNetworkId", assetNetworkId);
        payload.put("explicitOwnerIds", explicitOwnerIds);
        payload.put("idempotencyKey", key);
        var body = toJson(payload);
        var result = execute(HttpRequest.post("/v1/organizations/" + organizationId + "/payout-batches", body, true),
                CreatePayoutBatchResult.class);
        return result == null ? new CreatePayoutBatchResult(null) : result;
    }

    public PayoutBatchResponse getBatch(UUID organizationId, UUID payoutBatchId) {
        return execute(HttpRequest.get("/v1/organizations/" + organizationId + "/payout-batches/" + payoutBatchId),
                PayoutBatchResponse.class);
    }

    private String toJson(Object value) {
        try {
            return JsonCodec.mapper().writeValueAsString(value);
        } catch (Exception e) {
            throw new IllegalStateException("Failed to serialize request body", e);
        }
    }
}
