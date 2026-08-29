package com.ishtaran.sdk.model.dataplane;

import java.util.UUID;

/**
 * DEC-037, CUSTODY-EXECUTION-MODES.md — a beneficiary's valid on-chain receiving address for a
 * given AssetNetwork, consumed by {@code SelfCustodySettlementExecutionStrategy} when building an
 * execution leg. Deliberately NOT a withdrawal destination — no whitelist/cooldown policy,
 * first-registration-wins (a second registration for the same accountId+assetNetworkId is
 * rejected, never silently overwritten).
 */
public record RegisterExecutionDestinationResult(UUID executionDestinationId) {
}
