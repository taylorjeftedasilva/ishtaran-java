package com.ishtaran.sdk.resources;

import com.ishtaran.sdk.http.HttpRequest;
import com.ishtaran.sdk.http.HttpTransport;
import com.ishtaran.sdk.model.dataplane.SandboxBroadcastAttemptResponse;
import com.ishtaran.sdk.model.dataplane.SandboxBroadcastAttemptResult;
import com.ishtaran.sdk.model.dataplane.SandboxObservedAddressResponse;
import com.ishtaran.sdk.model.dataplane.SandboxObservedAddressResult;
import com.ishtaran.sdk.model.dataplane.SandboxTreasuryObservedBalanceResponse;
import com.ishtaran.sdk.model.enums.SimulatedBroadcastOutcome;
import com.ishtaran.sdk.serialization.JsonCodec;

import java.math.BigDecimal;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Data Plane, exclusive to {@code Environment} of type Sandbox — {@code Sandbox} (9 real routes).
 * Never available/valid against Production (the backend itself rejects with 422 if the Environment is
 * not Sandbox).
 */
public final class SandboxResource extends ApiResourceSupport {

    public SandboxResource(HttpTransport transport) {
        super(transport);
    }

    public SandboxObservedAddressResult faucet(UUID environmentId, String depositAddress, UUID assetNetworkId, BigDecimal amount) {
        var body = toJson(Map.of("depositAddress", depositAddress, "assetNetworkId", assetNetworkId, "amount", amount));
        return execute(HttpRequest.post("/v1/environments/" + environmentId + "/sandbox/faucet", body, false),
                SandboxObservedAddressResult.class);
    }

    public SandboxObservedAddressResult simulateDeposit(UUID environmentId, String depositAddress, UUID assetNetworkId, BigDecimal amount) {
        var body = toJson(Map.of("depositAddress", depositAddress, "assetNetworkId", assetNetworkId, "amount", amount));
        return execute(HttpRequest.post("/v1/environments/" + environmentId + "/sandbox/simulate-deposit", body, false),
                SandboxObservedAddressResult.class);
    }

    public void simulateConfirmation(UUID environmentId, UUID sandboxObservedAddressId, int additionalConfirmations, boolean isFinal) {
        var body = toJson(Map.of(
                "sandboxObservedAddressId", sandboxObservedAddressId,
                "additionalConfirmations", additionalConfirmations, "isFinal", isFinal));
        executeNoContent(HttpRequest.post("/v1/environments/" + environmentId + "/sandbox/simulate-confirmation", body, false));
    }

    public void simulateBroadcastConfirmation(UUID environmentId, UUID broadcastAttemptId, int additionalConfirmations, boolean isFinal) {
        var body = toJson(Map.of(
                "broadcastAttemptId", broadcastAttemptId,
                "additionalConfirmations", additionalConfirmations, "isFinal", isFinal));
        executeNoContent(HttpRequest.post("/v1/environments/" + environmentId + "/sandbox/simulate-broadcast-confirmation", body, false));
    }

    public SandboxBroadcastAttemptResult simulateWithdrawal(UUID environmentId, String destinationAddress, BigDecimal amount,
                                                              UUID assetNetworkId, SimulatedBroadcastOutcome outcome, String failureReason) {
        var payload = new LinkedHashMap<String, Object>();
        payload.put("destinationAddress", destinationAddress);
        payload.put("amount", amount);
        payload.put("assetNetworkId", assetNetworkId);
        payload.put("outcome", outcome.rawValue());
        payload.put("failureReason", failureReason);
        var body = toJson(payload);
        return execute(HttpRequest.post("/v1/environments/" + environmentId + "/sandbox/simulate-withdrawal", body, false),
                SandboxBroadcastAttemptResult.class);
    }

    public void setObservedTreasuryBalance(UUID environmentId, UUID assetNetworkId, BigDecimal balance) {
        var body = toJson(Map.of("assetNetworkId", assetNetworkId, "balance", balance));
        executeNoContent(HttpRequest.post("/v1/environments/" + environmentId + "/sandbox/treasury-balance", body, false));
    }

    public SandboxTreasuryObservedBalanceResponse getTreasuryBalance(UUID environmentId, UUID assetNetworkId) {
        return execute(HttpRequest.get("/v1/environments/" + environmentId + "/sandbox/treasury-balance/" + assetNetworkId),
                SandboxTreasuryObservedBalanceResponse.class);
    }

    public SandboxObservedAddressResponse getObservedAddress(UUID environmentId, UUID id) {
        return execute(HttpRequest.get("/v1/environments/" + environmentId + "/sandbox/observed-addresses/" + id),
                SandboxObservedAddressResponse.class);
    }

    public SandboxBroadcastAttemptResponse getBroadcastAttempt(UUID environmentId, UUID id) {
        return execute(HttpRequest.get("/v1/environments/" + environmentId + "/sandbox/broadcast-attempts/" + id),
                SandboxBroadcastAttemptResponse.class);
    }

    private String toJson(Object value) {
        try {
            return JsonCodec.mapper().writeValueAsString(value);
        } catch (Exception e) {
            throw new IllegalStateException("Failed to serialize request body", e);
        }
    }
}
