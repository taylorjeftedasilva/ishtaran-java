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
 * Control Plane (management) -- {@code WebhookEndpoints} (6 real routes). Always Member JWT -- does
 * not accept an API Key today (real Known Gap, see SDK_CAPABILITY_SPEC.md section 12.4). Signature
 * verification itself ({@link com.ishtaran.sdk.webhook.WebhookSignatureVerifier}) never makes an HTTP call.
 */
public final class WebhookEndpointsResource extends ApiResourceSupport {

    public WebhookEndpointsResource(HttpTransport transport) {
        super(transport);
    }

    /** {@code secret} is only returned here -- save it immediately, never retrievable afterward. */
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

    /** New {@code secret} -- the previous one immediately stops validating signatures. */
    public RotateWebhookEndpointSecretResult rotateSecret(UUID webhookEndpointId) {
        return execute(HttpRequest.post("/v1/webhook-endpoints/" + webhookEndpointId + "/rotate-secret", null, false),
                RotateWebhookEndpointSecretResult.class);
    }

    public void deactivate(UUID webhookEndpointId) {
        executeNoContent(HttpRequest.post("/v1/webhook-endpoints/" + webhookEndpointId + "/deactivate", null, false));
    }

    /**
     * {@code status} is sent as a NAME (string, case-insensitive) in the query string -- unlike the
     * {@code AssetNetworkCatalog} filter, which uses an integer (confirmed in source code:
     * {@code Enum.Parse<WebhookDeliveryStatus>(status, ignoreCase: true)}, see
     * SDK_CAPABILITY_SPEC.md section 9/11.3 for other examples of this same real per-route asymmetry).
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
            throw new IllegalStateException("Failed to serialize request body", e);
        }
    }
}
