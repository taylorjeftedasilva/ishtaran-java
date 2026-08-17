package com.ishtaran.sdk.resources;

import com.fasterxml.jackson.core.type.TypeReference;
import com.ishtaran.sdk.http.HttpRequest;
import com.ishtaran.sdk.http.HttpTransport;
import com.ishtaran.sdk.idempotency.IdempotencyKeyGenerator;
import com.ishtaran.sdk.model.dataplane.CreateWithdrawalDestinationResult;
import com.ishtaran.sdk.model.dataplane.WithdrawalQuoteResponse;
import com.ishtaran.sdk.model.dataplane.WithdrawalResponse;
import com.ishtaran.sdk.model.enums.WithdrawalStatus;
import com.ishtaran.sdk.pagination.PageIterator;
import com.ishtaran.sdk.serialization.JsonCodec;
import com.ishtaran.sdk.util.Polling;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

/**
 * Data Plane — {@code Withdrawals} (7 rotas reais). {@link #quote} nunca escreve nada (leitura pura
 * — ver SDK_CAPABILITY_SPEC.md §5); {@link #request} sempre expõe
 * {@code estimatedNetworkFee}/{@code estimatedRecipientAmount} na resposta, nunca esconde a Network
 * Fee.
 */
public final class WithdrawalsResource extends ApiResourceSupport {

    public WithdrawalsResource(HttpTransport transport) {
        super(transport);
    }

    public WithdrawalQuoteResponse quote(UUID organizationId, UUID accountId, UUID withdrawalDestinationId,
                                          UUID assetNetworkId, BigDecimal amount) {
        var body = toJson(Map.of(
                "accountId", accountId, "withdrawalDestinationId", withdrawalDestinationId,
                "assetNetworkId", assetNetworkId, "amount", amount));
        return execute(HttpRequest.post(
                "/v1/organizations/" + organizationId + "/withdrawals/quote", body, true),
                WithdrawalQuoteResponse.class);
    }

    public CreateWithdrawalDestinationResult createDestination(UUID organizationId, String address, UUID assetNetworkId) {
        var body = toJson(Map.of("address", address, "assetNetworkId", assetNetworkId));
        return execute(HttpRequest.post(
                "/v1/organizations/" + organizationId + "/withdrawal-destinations", body, false),
                CreateWithdrawalDestinationResult.class);
    }

    public WithdrawalResponse request(UUID organizationId, UUID accountId, UUID withdrawalDestinationId,
                                       UUID assetNetworkId, BigDecimal amount, String idempotencyKey) {
        var key = IdempotencyKeyGenerator.resolve(idempotencyKey);
        var body = toJson(Map.of(
                "accountId", accountId, "withdrawalDestinationId", withdrawalDestinationId,
                "assetNetworkId", assetNetworkId, "amount", amount, "idempotencyKey", key));
        return execute(HttpRequest.post(
                "/v1/organizations/" + organizationId + "/withdrawals", body, true),
                WithdrawalResponse.class);
    }

    public WithdrawalResponse get(UUID withdrawalId) {
        return execute(HttpRequest.get("/v1/withdrawals/" + withdrawalId), WithdrawalResponse.class);
    }

    /**
     * {@code skip}/{@code take} são paginação real (um dos únicos 2 endpoints do SDK com paginação
     * de verdade — ver SDK_CAPABILITY_SPEC.md §12.7).
     */
    public List<WithdrawalResponse> list(UUID organizationId, WithdrawalStatus status,
                                          OffsetDateTime from, OffsetDateTime to, Integer skip, Integer take) {
        var query = new StringBuilder("/v1/organizations/" + organizationId + "/withdrawals?");
        if (status != null) {
            query.append("status=").append(status.rawValue()).append('&');
        }
        if (from != null) {
            query.append("from=").append(from).append('&');
        }
        if (to != null) {
            query.append("to=").append(to).append('&');
        }
        if (skip != null) {
            query.append("skip=").append(skip).append('&');
        }
        if (take != null) {
            query.append("take=").append(take).append('&');
        }
        return execute(HttpRequest.get(query.toString()), new TypeReference<List<WithdrawalResponse>>() {
        });
    }

    public void cancel(UUID withdrawalId, String reason) {
        var body = toJson(Map.of("reason", reason == null ? "" : reason));
        executeNoContent(HttpRequest.post("/v1/withdrawals/" + withdrawalId + "/cancel", body, false));
    }

    public void retryBroadcast(UUID withdrawalId) {
        executeNoContent(HttpRequest.post("/v1/withdrawals/" + withdrawalId + "/retry-broadcast", null, false));
    }

    /**
     * Iterador lazy (busca próxima página sob demanda, nunca carrega tudo de uma vez) — ver
     * SDK_CAPABILITY_SPEC.md §12.7, um dos únicos 2 endpoints com paginação real do SDK.
     */
    public PageIterator<WithdrawalResponse> listAll(UUID organizationId, WithdrawalStatus status,
                                                      OffsetDateTime from, OffsetDateTime to, int pageSize) {
        return new PageIterator<>(pageSize, (skip, take) -> list(organizationId, status, from, to, skip, take));
    }

    private static final Set<WithdrawalStatus> TERMINAL_STATUSES = Set.of(
            WithdrawalStatus.COMPLETED, WithdrawalStatus.REJECTED,
            WithdrawalStatus.CANCELLED, WithdrawalStatus.BROADCAST_FAILED);

    /**
     * Polling seguro, nunca infinito — sempre {@code timeout}/{@code pollInterval} explícitos (ver
     * SDK_CAPABILITY_SPEC.md §15). Termina em qualquer estado terminal real de
     * {@link WithdrawalStatus} (Completed/Rejected/Cancelled/BroadcastFailed).
     */
    public WithdrawalResponse waitFor(UUID withdrawalId, Duration timeout, Duration pollInterval) {
        return Polling.until(() -> get(withdrawalId), r -> TERMINAL_STATUSES.contains(r.status()),
                timeout, pollInterval, "withdrawalId=" + withdrawalId);
    }

    private String toJson(Object value) {
        try {
            return JsonCodec.mapper().writeValueAsString(value);
        } catch (Exception e) {
            throw new IllegalStateException("Falha ao serializar corpo de requisição", e);
        }
    }
}
