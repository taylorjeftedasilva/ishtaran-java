package com.ishtaran.sdk.resources;

import com.ishtaran.sdk.http.HttpRequest;
import com.ishtaran.sdk.http.HttpTransport;
import com.ishtaran.sdk.idempotency.IdempotencyKeyGenerator;
import com.ishtaran.sdk.model.dataplane.AllocatedDepositAddressResult;
import com.ishtaran.sdk.model.dataplane.RegisterWalletResult;
import com.ishtaran.sdk.model.dataplane.WalletPublicMaterialResult;
import com.ishtaran.sdk.model.dataplane.WalletResponse;
import com.ishtaran.sdk.model.enums.DerivationScheme;
import com.ishtaran.sdk.serialization.JsonCodec;

import java.util.LinkedHashMap;
import java.util.UUID;

/**
 * Data Plane — {@code ExecutionCustody} Wallets (SPEC-018/021, checkpoint 7). The SDK only sends the
 * extended PUBLIC key ({@code publicDerivationMaterial}) — generated locally by
 * {@link com.ishtaran.sdk.wallet.WalletFactory}, never the private key/mnemonic (INV-SC-01).
 */
public final class WalletsResource extends ApiResourceSupport {

    public WalletsResource(HttpTransport transport) {
        super(transport);
    }

    public RegisterWalletResult register(UUID applicationId, UUID networkId, DerivationScheme scheme,
                                          String publicDerivationMaterial, String idempotencyKey) {
        var payload = new LinkedHashMap<String, Object>();
        payload.put("networkId", networkId);
        payload.put("scheme", scheme.rawValue());
        payload.put("publicDerivationMaterial", publicDerivationMaterial);
        payload.put("idempotencyKey", IdempotencyKeyGenerator.resolve(idempotencyKey));
        var body = toJson(payload);
        return execute(HttpRequest.post("/v1/applications/" + applicationId + "/wallets", body, true), RegisterWalletResult.class);
    }

    /** BR-WLT-002 — never includes {@code publicDerivationMaterial}; see {@link #getPublicMaterial}. */
    public WalletResponse get(UUID walletId) {
        return execute(HttpRequest.get("/v1/wallets/" + walletId), WalletResponse.class);
    }

    public WalletPublicMaterialResult getPublicMaterial(UUID walletId) {
        return execute(HttpRequest.get("/v1/wallets/" + walletId + "/public-material"), WalletPublicMaterialResult.class);
    }

    /**
     * SPEC-018 §BR-WLT-001 — each call allocates a NEW index by design (never idempotent by
     * nature, no {@code idempotencyKey} — same convention as the backend); never reuse on
     * automatic retry.
     */
    public AllocatedDepositAddressResult allocateDepositAddress(UUID applicationId, UUID networkId) {
        var payload = new LinkedHashMap<String, Object>();
        payload.put("networkId", networkId);
        var body = toJson(payload);
        return execute(HttpRequest.post("/v1/applications/" + applicationId + "/wallets/deposit-addresses", body, false),
                AllocatedDepositAddressResult.class);
    }

    private String toJson(Object value) {
        try {
            return JsonCodec.mapper().writeValueAsString(value);
        } catch (Exception e) {
            throw new IllegalStateException("Failed to serialize request body", e);
        }
    }
}
