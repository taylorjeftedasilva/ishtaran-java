package com.ishtaran.sdk.resources;

import com.ishtaran.sdk.http.HttpRequest;
import com.ishtaran.sdk.http.HttpTransport;
import com.ishtaran.sdk.model.dataplane.RegisterExecutionDestinationResult;
import com.ishtaran.sdk.serialization.JsonCodec;

import java.util.LinkedHashMap;
import java.util.UUID;

/**
 * Data Plane — {@code ExecutionCustody} ExecutionDestinations (DEC-037, CUSTODY-EXECUTION-MODES.md).
 * Registers the on-chain address a beneficiary {@code Account} actually receives funds at, for a
 * given AssetNetwork — required before a {@code Settlement} involving that Account can execute
 * under SelfCustody (the backend fails fast, before Signing/Broadcast, if none is registered).
 */
public final class ExecutionDestinationsResource extends ApiResourceSupport {

    public ExecutionDestinationsResource(HttpTransport transport) {
        super(transport);
    }

    public RegisterExecutionDestinationResult register(UUID organizationId, UUID accountId, UUID assetNetworkId, String address) {
        var payload = new LinkedHashMap<String, Object>();
        payload.put("accountId", accountId);
        payload.put("assetNetworkId", assetNetworkId);
        payload.put("address", address);
        var body = toJson(payload);
        return execute(HttpRequest.post("/v1/organizations/" + organizationId + "/execution-destinations", body, false),
                RegisterExecutionDestinationResult.class);
    }

    private String toJson(Object value) {
        try {
            return JsonCodec.mapper().writeValueAsString(value);
        } catch (Exception e) {
            throw new IllegalStateException("Failed to serialize request body", e);
        }
    }
}
