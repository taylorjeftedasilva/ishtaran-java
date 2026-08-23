package com.ishtaran.sdk.resources;

import com.ishtaran.sdk.http.HttpRequest;
import com.ishtaran.sdk.http.HttpTransport;
import com.ishtaran.sdk.idempotency.IdempotencyKeyGenerator;
import com.ishtaran.sdk.model.dataplane.CreateSigningRequestResult;
import com.ishtaran.sdk.model.dataplane.ExecutionLegInput;
import com.ishtaran.sdk.model.dataplane.SigningRequestResponse;
import com.ishtaran.sdk.model.dataplane.SubmitSignedTransactionResult;
import com.ishtaran.sdk.serialization.JsonCodec;

import java.time.OffsetDateTime;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.UUID;

/**
 * Data Plane — {@code ExecutionCustody} SigningRequests (SPEC-019/020/021, checkpoint 7). The SDK
 * never computes the canonical hash on create — the backend computes it and returns it in {@link #get};
 * the SDK only signs locally ({@link com.ishtaran.sdk.wallet.Signer#sign}) and submits it back.
 */
public final class SigningRequestsResource extends ApiResourceSupport {

    public SigningRequestsResource(HttpTransport transport) {
        super(transport);
    }

    public CreateSigningRequestResult create(UUID environmentId, UUID walletId, long derivationReference,
                                              String originReference, UUID assetNetworkId, String sourceAddress,
                                              List<ExecutionLegInput> legs, OffsetDateTime expiresAt, String idempotencyKey) {
        var payload = new LinkedHashMap<String, Object>();
        payload.put("walletId", walletId);
        payload.put("derivationReference", derivationReference);
        payload.put("originReference", originReference);
        payload.put("assetNetworkId", assetNetworkId);
        payload.put("sourceAddress", sourceAddress);
        payload.put("legs", legs);
        payload.put("expiresAt", expiresAt);
        payload.put("idempotencyKey", IdempotencyKeyGenerator.resolve(idempotencyKey));
        var body = toJson(payload);
        return execute(HttpRequest.post("/v1/environments/" + environmentId + "/signing-requests", body, true),
                CreateSigningRequestResult.class);
    }

    public SigningRequestResponse get(UUID signingRequestId) {
        return execute(HttpRequest.get("/v1/signing-requests/" + signingRequestId), SigningRequestResponse.class);
    }

    /**
     * SPEC-020/INV-SC-03 — {@code submittedCanonicalHash} must be exactly the {@code canonicalHash}
     * returned by {@link #get} for that Leg (compared byte by byte by the backend before
     * verifying the signature, fail fast). {@code signature} is the output of
     * {@link com.ishtaran.sdk.wallet.Signer#sign(long, byte[])}, encoded in uppercase hex.
     */
    public SubmitSignedTransactionResult submitSignedTransaction(UUID signingRequestId, UUID executionLegId,
                                                                   String submittedCanonicalHash, String signatureHex) {
        var payload = new LinkedHashMap<String, Object>();
        payload.put("submittedCanonicalHash", submittedCanonicalHash);
        payload.put("signature", signatureHex);
        var body = toJson(payload);
        return execute(
                HttpRequest.post("/v1/signing-requests/" + signingRequestId + "/legs/" + executionLegId + "/submit", body, false),
                SubmitSignedTransactionResult.class);
    }

    private String toJson(Object value) {
        try {
            return JsonCodec.mapper().writeValueAsString(value);
        } catch (Exception e) {
            throw new IllegalStateException("Failed to serialize request body", e);
        }
    }
}
