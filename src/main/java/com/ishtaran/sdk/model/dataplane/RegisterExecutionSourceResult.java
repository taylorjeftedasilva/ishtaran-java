package com.ishtaran.sdk.model.dataplane;

import java.util.UUID;

/**
 * SPEC-ADDRESSPOOL-001, CUSTODY-EXECUTION-MODES.md — the outbound-only counterpart of a Wallet
 * derivation: the address ExecutionCustody signs FROM to pay network cost (Energy/Bandwidth/gas),
 * never confused with an ExecutionDestination (a beneficiary's inbound address). Must be
 * registered before the first self-custody Withdrawal/Payout on a given AssetNetwork — see
 * {@code docs/specs/execution-custody/README.md} "Bootstrap obrigatório" for the required order
 * (Wallet -&gt; ExecutionSource -&gt; NetworkCostPayerAccount).
 */
public record RegisterExecutionSourceResult(UUID executionSourceId) {
}
