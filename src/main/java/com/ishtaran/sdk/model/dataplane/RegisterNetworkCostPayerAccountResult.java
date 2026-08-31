package com.ishtaran.sdk.model.dataplane;

import java.util.UUID;

/**
 * SPEC-NETEXEC-001 — the Account debited for the *charged* network cost ({@code totalCharged}, in
 * {@code quoteCurrency}) once a NetworkExecutionQuote is authorized. First-registration-wins per
 * (organizationId, assetNetworkId), same as ExecutionDestination — never silently overwritten.
 * Must belong to the same Organization as the caller (a cross-tenant accountId is rejected).
 */
public record RegisterNetworkCostPayerAccountResult(UUID networkCostPayerAccountId) {
}
