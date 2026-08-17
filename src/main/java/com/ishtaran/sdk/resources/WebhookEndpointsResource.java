package com.ishtaran.sdk.resources;

import com.fasterxml.jackson.core.type.TypeReference;
import com.ishtaran.sdk.http.HttpRequest;
import com.ishtaran.sdk.http.HttpTransport;
import com.ishtaran.sdk.model.dataplane.ConfigureWebhookEndpointResult;
import com.ishtaran.sdk.model.dataplane.RotateWebhookEndpointSecretResult;
import com.ishtaran.sdk.model.dataplane.WebhookDeliveryResponse;
import com.ishtaran.sdk.model.dataplane.WebhookEndpointResponse;
import com.ishtaran.sdk.model.enums.WebhookDeliveryStatus;
import com.ishtaran.sdk.serialization.JsonCodec;

import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Control Plane (gestão) — {@code WebhookEndpoints} (6 rotas reais). Sempre Member JWT — não aceita
 * API Key hoje (Known Gap real, ver SDK_CAPABILITY_SPEC.md §12.4). Verificação de assinatura em si
 * ({@link com.ishtaran.sdk.webhook.WebhookSignatureVerifier}) nunca faz chamada HTTP.
 */
public final class WebhookEndpointsResource extends ApiResourceSupport {

    public WebhookEndpointsResource(HttpTransport transport) {
        super(transport);
    }

    /** {@code secret} só é retornado aqui — guarde-o imediatamente, nunca recuperável depois. */
    public ConfigureWebhookEndpointResult create(UUID organizationId, String url) {
        var body = toJson(Map.of("url", url));
        return execute(HttpRequest.post("/v1/organizations/" + organizationId + "/webhook-endpoints", body, false),
                ConfigureWebhookEndpointResult.class);
    }

    public List<WebhookEndpointResponse> list(UUID organizationId) {
        return execute(HttpRequest.get("/v1/organizations/" + organizationId + "/webhook-endpoints"),
                new TypeReference<List<WebhookEndpointResponse>>() {
                });
    }

    public WebhookEndpointResponse get(UUID webhookEndpointId) {
        return execute(HttpRequest.get("/v1/webhook-endpoints/" + webhookEndpointId), WebhookEndpointResponse.class);
    }

    /** Novo {@code secret} — o anterior deixa de validar assinaturas imediatamente. */
    public RotateWebhookEndpointSecretResult rotateSecret(UUID webhookEndpointId) {
        return execute(HttpRequest.post("/v1/webhook-endpoints/" + webhookEndpointId + "/rotate-secret", null, false),
                RotateWebhookEndpointSecretResult.class);
    }

    public void deactivate(UUID webhookEndpointId) {
        executeNoContent(HttpRequest.post("/v1/webhook-endpoints/" + webhookEndpointId + "/deactivate", null, false));
    }

    /**
     * {@code status} é enviado como NOME (string, case-insensitive) na query string — diferente do
     * filtro de {@code AssetNetworkCatalog} que usa inteiro (confirmado em código-fonte:
     * {@code Enum.Parse<WebhookDeliveryStatus>(status, ignoreCase: true)}, ver
     * SDK_CAPABILITY_SPEC.md §9/§11.3 para outros exemplos dessa mesma assimetria real por rota).
     */
    public List<WebhookDeliveryResponse> listDeliveries(UUID webhookEndpointId, String eventType, WebhookDeliveryStatus status) {
        var query = new StringBuilder("/v1/webhook-endpoints/" + webhookEndpointId + "/deliveries?");
        if (eventType != null) {
            query.append("eventType=")
                    .append(java.net.URLEncoder.encode(eventType, java.nio.charset.StandardCharsets.UTF_8))
                    .append('&');
        }
        if (status != null) {
            query.append("status=").append(status.name()).append('&');
        }
        return execute(HttpRequest.get(query.toString()), new TypeReference<List<WebhookDeliveryResponse>>() {
        });
    }

    private String toJson(Object value) {
        try {
            return JsonCodec.mapper().writeValueAsString(value);
        } catch (Exception e) {
            throw new IllegalStateException("Falha ao serializar corpo de requisição", e);
        }
    }
}
