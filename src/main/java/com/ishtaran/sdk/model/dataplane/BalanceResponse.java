package com.ishtaran.sdk.model.dataplane;

import java.math.BigDecimal;

/**
 * Mirrors {@code Ledger.Contracts.Responses.BalanceResponse} — does not appear in the generated
 * OpenAPI (the real route uses {@code Results.Ok(...)} without {@code .Produces<T>()}, so
 * Swashbuckle cannot infer the schema; extracted directly from the source code, never invented).
 *
 * <p>G.2 (found 2026-09-11, SDK audit): {@code payable}/{@code reservedForPayout}/
 * {@code delivered} have been part of the real backend record since {@code SPEC-024/025}
 * (2026-08-30) — previously silently dropped here (only {@code available}/{@code pending}/
 * {@code reserved} were ever declared). Payable is what Payout owes a beneficiary but hasn't paid
 * yet (an economic obligation, never an on-chain balance); reservedForPayout is Payable already
 * claimed by an in-flight PayoutBatch; delivered is the cumulative real payout total — under
 * SelfCustody this can grow while available stays exactly 0, because the money already left the
 * platform's custody entirely (see {@code WalletBalanceResource} for the wallet's own on-chain
 * state, a different question again). The compact constructor below null-coalesces all three to
 * {@link BigDecimal#ZERO} — Jackson binds records via their canonical constructor directly, so an
 * older wire response that omits them still deserializes cleanly to a real zero, never a
 * {@code null} a caller could NPE on.
 */
public record BalanceResponse(
        BigDecimal available,
        BigDecimal pending,
        BigDecimal reserved,
        BigDecimal payable,
        BigDecimal reservedForPayout,
        BigDecimal delivered) {

    public BalanceResponse {
        payable = payable == null ? BigDecimal.ZERO : payable;
        reservedForPayout = reservedForPayout == null ? BigDecimal.ZERO : reservedForPayout;
        delivered = delivered == null ? BigDecimal.ZERO : delivered;
    }
}
