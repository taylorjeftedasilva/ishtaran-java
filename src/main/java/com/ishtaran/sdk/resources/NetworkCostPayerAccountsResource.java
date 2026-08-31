package com.ishtaran.sdk.resources;

import com.ishtaran.sdk.http.HttpRequest;
import com.ishtaran.sdk.http.HttpTransport;
import com.ishtaran.sdk.model.dataplane.RegisterNetworkCostPayerAccountResult;
import com.ishtaran.sdk.serialization.JsonCodec;

import java.util.LinkedHashMap;
import java.util.UUID;

/**
 * Data Plane — {@code ExecutionCustody} NetworkCostPayerAccounts (SPEC-NETEXEC-001). Registers
 * the Account debited for the *charged* network cost of a NetworkExecutionQuote
 * ({@code totalCharged}, in {@code quoteCurrency}). {@code accountId} must belong to the
 * caller's own Organization — a cross-tenant Account is rejected. First-registration-wins per
 * (organizationId, assetNetworkId).
 */
public final class NetworkCostPayerAccountsResource extends ApiResourceSupport {

    public NetworkCostPayerAccountsResource(HttpTransport transport) {
        super(transport);
    }

    public RegisterNetworkCostPayerAccountResult register(UUID organizationId, UUID assetNetworkId, UUID accountId) {
        var payload = new LinkedHashMap<String, Object>();
        payload.put("assetNetworkId", assetNetworkId);
        payload.put("accountId", accountId);
        var body = toJson(payload);
        return execute(HttpRequest.post("/v1/organizations/" + organizationId + "/network-cost-payer-accounts", body, false),
                RegisterNetworkCostPayerAccountResult.class);
    }

    private String toJson(Object value) {
        try {
            return JsonCodec.mapper().writeValueAsString(value);
        } catch (Exception e) {
            throw new IllegalStateException("Failed to serialize request body", e);
        }
    }
}
