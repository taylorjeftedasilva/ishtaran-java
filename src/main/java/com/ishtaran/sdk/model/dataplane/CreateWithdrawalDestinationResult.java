package com.ishtaran.sdk.model.dataplane;

import java.util.UUID;

/**
 * A rota real ({@code CreateWithdrawalDestination} em {@code WithdrawalsEndpoints.cs}) devolve um
 * objeto anônimo {@code { withdrawalDestinationId }} — por isso não aparece com schema no OpenAPI
 * gerado (limitação real da geração, não do SDK). Este record espelha o shape real observado no
 * código-fonte do handler.
 */
public record CreateWithdrawalDestinationResult(UUID withdrawalDestinationId) {
}
