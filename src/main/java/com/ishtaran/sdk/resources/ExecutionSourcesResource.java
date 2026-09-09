package com.ishtaran.sdk.resources;

import com.ishtaran.sdk.http.HttpRequest;
import com.ishtaran.sdk.http.HttpTransport;
import com.ishtaran.sdk.model.dataplane.RegisterExecutionSourceResult;
import com.ishtaran.sdk.serialization.JsonCodec;

import java.math.BigDecimal;
import java.util.LinkedHashMap;
import java.util.UUID;

/**
 * Data Plane — {@code ExecutionCustody} ExecutionSources (CUSTODY-EXECUTION-MODES.md, SPEC-ADDRESSPOOL-001).
 * Registers the address ExecutionCustody signs FROM to pay network cost for a given AssetNetwork —
 * required, together with a {@link NetworkCostPayerAccountsResource}, before the first
 * self-custody Withdrawal/Payout on that AssetNetwork (the backend fails fast if none is
 * registered).
 */
public final class ExecutionSourcesResource extends ApiResourceSupport {

    public ExecutionSourcesResource(HttpTransport transport) {
        super(transport);
    }

    public RegisterExecutionSourceResult register(UUID organizationId, UUID environmentId, UUID assetNetworkId,
                                                   UUID walletId, long derivationReference, String address) {
        var payload = new LinkedHashMap<String, Object>();
        payload.put("environmentId", environmentId);
        payload.put("assetNetworkId", assetNetworkId);
        payload.put("walletId", walletId);
        payload.put("derivationReference", derivationReference);
        payload.put("address", address);
        var body = toJson(payload);
        return execute(HttpRequest.post("/v1/organizations/" + organizationId + "/execution-sources", body, false),
                RegisterExecutionSourceResult.class);
    }

    /**
     * F.18 — self-reported (no on-chain verification in this version) declaration of the
     * on-chain resource capacity available to the Wallet backing this ExecutionSource. Required
     * before CUSTOMER_RESOURCES (SELF) mode can ever succeed for it — the platform checks this
     * declared stake for sufficiency at quote time. Safe to call again any time to re-sync (no
     * first-registration-wins restriction, unlike {@link #register}).
     */
    public void syncResourceStake(UUID organizationId, UUID executionSourceId, BigDecimal availableNativeAmount,
                                   BigDecimal availableEnergy, BigDecimal availableBandwidth) {
        var payload = new LinkedHashMap<String, Object>();
        payload.put("availableNativeAmount", availableNativeAmount);
        payload.put("availableEnergy", availableEnergy);
        payload.put("availableBandwidth", availableBandwidth);
        var body = toJson(payload);
        executeNoContent(HttpRequest.post(
                "/v1/organizations/" + organizationId + "/execution-sources/" + executionSourceId + "/resource-stake", body, false));
    }

    private String toJson(Object value) {
        try {
            return JsonCodec.mapper().writeValueAsString(value);
        } catch (Exception e) {
            throw new IllegalStateException("Failed to serialize request body", e);
        }
    }
}
