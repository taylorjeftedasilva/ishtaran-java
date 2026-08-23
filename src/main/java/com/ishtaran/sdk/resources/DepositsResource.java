package com.ishtaran.sdk.resources;

import com.ishtaran.sdk.http.HttpRequest;
import com.ishtaran.sdk.http.HttpTransport;
import com.ishtaran.sdk.idempotency.IdempotencyKeyGenerator;
import com.ishtaran.sdk.model.dataplane.CreatePaymentIntentResult;
import com.ishtaran.sdk.model.dataplane.DepositResponse;
import com.ishtaran.sdk.model.dataplane.PaymentIntentResponse;
import com.ishtaran.sdk.serialization.JsonCodec;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.LinkedHashMap;
import java.util.UUID;

/** Data Plane — {@code Deposits} (3 real routes). */
public final class DepositsResource extends ApiResourceSupport {

    public DepositsResource(HttpTransport transport) {
        super(transport);
    }

    /**
     * The real {@code depositAddress} is only exposed by the dedicated GET ({@link #getPaymentIntent})
     * afterwards — never in this response's body (see {@link CreatePaymentIntentResult}).
     */
    public CreatePaymentIntentResult createPaymentIntent(UUID organizationId, UUID transactionId, UUID assetNetworkId,
                                                           BigDecimal amount, OffsetDateTime expiresAt, String idempotencyKey) {
        var payload = new LinkedHashMap<String, Object>();
        payload.put("transactionId", transactionId);
        payload.put("assetNetworkId", assetNetworkId);
        payload.put("amount", amount);
        payload.put("expiresAt", expiresAt);
        payload.put("idempotencyKey", IdempotencyKeyGenerator.resolve(idempotencyKey));
        var body = toJson(payload);
        return execute(HttpRequest.post("/v1/organizations/" + organizationId + "/payment-intents", body, true),
                CreatePaymentIntentResult.class);
    }

    public PaymentIntentResponse getPaymentIntent(UUID paymentIntentId) {
        return execute(HttpRequest.get("/v1/payment-intents/" + paymentIntentId), PaymentIntentResponse.class);
    }

    public DepositResponse getDeposit(UUID depositId) {
        return execute(HttpRequest.get("/v1/deposits/" + depositId), DepositResponse.class);
    }

    private String toJson(Object value) {
        try {
            return JsonCodec.mapper().writeValueAsString(value);
        } catch (Exception e) {
            throw new IllegalStateException("Failed to serialize request body", e);
        }
    }
}
