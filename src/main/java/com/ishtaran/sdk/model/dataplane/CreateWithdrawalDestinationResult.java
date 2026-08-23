package com.ishtaran.sdk.model.dataplane;

import java.util.UUID;

/**
 * The real route ({@code CreateWithdrawalDestination} in {@code WithdrawalsEndpoints.cs}) returns
 * an anonymous object {@code { withdrawalDestinationId }} — that's why it does not appear with a
 * schema in the generated OpenAPI (a real generation limitation, not the SDK's). This record
 * mirrors the real shape observed in the handler's source code.
 */
public record CreateWithdrawalDestinationResult(UUID withdrawalDestinationId) {
}
