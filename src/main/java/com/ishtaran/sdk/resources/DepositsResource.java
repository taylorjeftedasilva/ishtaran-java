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

/** Data Plane — {@code Deposits} (3 rotas reais). */
public final class DepositsResource extends ApiResourceSupport {

    public DepositsResource(HttpTransport transport) {
        super(transport);
    }

    /**
     * O {@code depositAddress} real só é exposto pelo GET dedicado ({@link #getPaymentIntent}) em
     * seguida — nunca no corpo desta resposta (ver {@link CreatePaymentIntentResult}).
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
            throw new IllegalStateException("Falha ao serializar corpo de requisição", e);
        }
    }
}
