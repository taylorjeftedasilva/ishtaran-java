package com.ishtaran.sdk.resources;

import com.fasterxml.jackson.core.type.TypeReference;
import com.ishtaran.sdk.http.HttpRequest;
import com.ishtaran.sdk.http.HttpTransport;
import com.ishtaran.sdk.model.dataplane.AccountResponse;
import com.ishtaran.sdk.model.dataplane.CreateAccountResult;
import com.ishtaran.sdk.serialization.JsonCodec;

import java.util.List;
import java.util.Map;
import java.util.UUID;

/** Data Plane — {@code Accounts} (7 rotas reais). */
public final class AccountsResource extends ApiResourceSupport {

    public AccountsResource(HttpTransport transport) {
        super(transport);
    }

    public CreateAccountResult create(UUID organizationId, String externalId) {
        var body = toJson(Map.of("externalId", externalId));
        return execute(HttpRequest.post("/v1/organizations/" + organizationId + "/accounts", body, false),
                CreateAccountResult.class);
    }

    public List<AccountResponse> list(UUID organizationId) {
        return execute(HttpRequest.get("/v1/organizations/" + organizationId + "/accounts"),
                new TypeReference<List<AccountResponse>>() {
                });
    }

    public AccountResponse get(UUID accountId) {
        return execute(HttpRequest.get("/v1/accounts/" + accountId), AccountResponse.class);
    }

    public void authorizeApplication(UUID accountId, UUID applicationId) {
        var body = toJson(Map.of("applicationId", applicationId));
        executeNoContent(HttpRequest.post("/v1/accounts/" + accountId + "/authorize-application", body, false));
    }

    public void freeze(UUID accountId, String reason) {
        var body = toJson(Map.of("reason", reason == null ? "" : reason));
        executeNoContent(HttpRequest.post("/v1/accounts/" + accountId + "/freeze", body, false));
    }

    public void unfreeze(UUID accountId) {
        executeNoContent(HttpRequest.post("/v1/accounts/" + accountId + "/unfreeze", null, false));
    }

    public void close(UUID accountId) {
        executeNoContent(HttpRequest.delete("/v1/accounts/" + accountId));
    }

    private String toJson(Object value) {
        try {
            return JsonCodec.mapper().writeValueAsString(value);
        } catch (Exception e) {
            throw new IllegalStateException("Falha ao serializar corpo de requisição", e);
        }
    }
}
