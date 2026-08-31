package com.ishtaran.sdk.resources;

import com.ishtaran.sdk.http.HttpRequest;
import com.ishtaran.sdk.http.HttpTransport;
import com.ishtaran.sdk.model.dataplane.NetworkExecutionOperationInput;
import com.ishtaran.sdk.model.dataplane.NetworkExecutionQuoteResponse;
import com.ishtaran.sdk.model.enums.NetworkCostPayer;
import com.ishtaran.sdk.serialization.JsonCodec;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Data Plane — {@code ExecutionCustody} Network Execution Engine (SPEC-NETEXEC-001). A quote is a
 * priced, time-boxed plan for 1..N physical on-chain operations; it never writes anything by
 * itself (Settlement/Withdrawal/Payout each get/re-get their own quote internally at execution
 * time — {@code preview quote != execution quote}, never reuse this response as a price guarantee).
 */
public final class NetworkExecutionResource extends ApiResourceSupport {

    public NetworkExecutionResource(HttpTransport transport) {
        super(transport);
    }

    public NetworkExecutionQuoteResponse quote(UUID environmentId, UUID assetNetworkId,
                                                List<NetworkExecutionOperationInput> operations, NetworkCostPayer networkCostPayer) {
        var payload = new LinkedHashMap<String, Object>();
        payload.put("assetNetworkId", assetNetworkId);
        payload.put("operations", operations == null ? null : operations.stream().map(this::toOperationPayload).collect(Collectors.toList()));
        payload.put("networkCostPayer", networkCostPayer);
        var body = toJson(payload);
        return execute(HttpRequest.post("/v1/environments/" + environmentId + "/network-execution-quote", body, true),
                NetworkExecutionQuoteResponse.class);
    }

    private Map<String, Object> toOperationPayload(NetworkExecutionOperationInput op) {
        var payload = new LinkedHashMap<String, Object>();
        payload.put("destinationAddress", op.destinationAddress());
        payload.put("amount", op.amount());
        payload.put("kind", op.kind());
        payload.put("reference", op.reference());
        return payload;
    }

    private String toJson(Object value) {
        try {
            return JsonCodec.mapper().writeValueAsString(value);
        } catch (Exception e) {
            throw new IllegalStateException("Failed to serialize request body", e);
        }
    }
}
