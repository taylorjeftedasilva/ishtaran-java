package com.ishtaran.sdk.resources;

import com.ishtaran.sdk.http.HttpRequest;
import com.ishtaran.sdk.http.HttpTransport;
import com.ishtaran.sdk.idempotency.IdempotencyKeyGenerator;
import com.ishtaran.sdk.model.dataplane.CreateTransferAck;
import com.ishtaran.sdk.model.dataplane.TransferResponse;
import com.ishtaran.sdk.serialization.JsonCodec;

import java.math.BigDecimal;
import java.util.LinkedHashMap;
import java.util.UUID;

/**
 * PROMPT 7/7.1 (SPEC-TRANSFER-001/002) — first-class Transfer: Account/Wallet -&gt; asset -&gt;
 * another internal Account or an arbitrary external address, never a Payment/PaymentIntent/
 * Settlement in disguise. Exactly one of {@code destinationAccountId}/{@code destinationAddress}
 * must be given — an external destination never needs to be pre-registered (unlike
 * {@code withdrawals().createDestination}). Platform Fee ({@code platformFeeAmount} on the
 * response) is always ON_TOP — {@code amount} is exactly what the recipient receives, the fee is
 * charged separately from the sender.
 *
 * <p>BR-TRF-008 (PROMPT 7.1) — {@link #request} ends in {@code AWAITING_SIGNATURE}, never
 * {@code CONFIRMED} synchronously: a real {@code SigningRequest} is created for the source
 * Account's own execution/signing identity (see {@code WalletsResource#registerForAccount}), and
 * the client still has to fetch it ({@code client.signingRequests().get(signingRequestId)}), sign
 * each Leg locally, and submit it back before the Transfer confirms.
 */
public final class TransfersResource extends ApiResourceSupport {

    public TransfersResource(HttpTransport transport) {
        super(transport);
    }

    /**
     * The creation endpoint itself only ever acknowledges {@code {transferId}} (same convention as
     * every other {@code POST .../transfers}-shaped route in this platform, e.g. Settlement/
     * Withdrawal) — never the full record. This method does the create, then immediately follows
     * up with {@link #get} so the caller receives a genuinely populated {@link TransferResponse}
     * (status, signingRequestId, platformFeeAmount, ...) in one call. Found live (2026-09-12): an
     * earlier version deserialized the bare create ack itself as a {@code TransferResponse},
     * silently producing a mostly-null object instead of a real one.
     */
    public TransferResponse request(UUID organizationId, UUID applicationId, UUID environmentId, UUID sourceAccountId,
                                     UUID assetNetworkId, BigDecimal amount, UUID destinationAccountId,
                                     String destinationAddress, String idempotencyKey) {
        var payload = new LinkedHashMap<String, Object>();
        payload.put("applicationId", applicationId);
        payload.put("environmentId", environmentId);
        payload.put("sourceAccountId", sourceAccountId);
        payload.put("assetNetworkId", assetNetworkId);
        payload.put("amount", amount);
        payload.put("destinationAccountId", destinationAccountId);
        payload.put("destinationAddress", destinationAddress);
        payload.put("idempotencyKey", IdempotencyKeyGenerator.resolve(idempotencyKey));
        var body = toJson(payload);
        var created = execute(HttpRequest.post("/v1/organizations/" + organizationId + "/transfers", body, true), CreateTransferAck.class);
        return get(created.transferId());
    }

    public TransferResponse get(UUID transferId) {
        return execute(HttpRequest.get("/v1/transfers/" + transferId), TransferResponse.class);
    }

    private String toJson(Object value) {
        try {
            return JsonCodec.mapper().writeValueAsString(value);
        } catch (Exception e) {
            throw new IllegalStateException("Failed to serialize request body", e);
        }
    }
}
