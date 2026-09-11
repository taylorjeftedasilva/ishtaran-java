package com.ishtaran.sdk.model.dataplane;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;

/**
 * Ishtaran Wallet Balance / On-Chain Balance capability — the wallet's own on-chain balance,
 * NEVER the Ledger ({@link com.ishtaran.sdk.resources.LedgerResource#getBalance}). {@code
 * accountId} throughout this SDK surface is the walletId — {@code ExecutionDestination} (the
 * registered self-custody address) is already the platform's real 1:1 (accountId,
 * assetNetworkId) -> address source of truth, so no separate "wallet registration" concept was
 * introduced — see {@link com.ishtaran.sdk.resources.WalletBalanceResource}'s own doc.
 */
public record WalletBalanceResponse(
        UUID accountId,
        UUID assetNetworkId,
        String address,
        BigDecimal balance,
        /** Null if this wallet has never been successfully observed yet -- balance is 0 in that case, never a lie. */
        OffsetDateTime observedAt,
        String blockReference,
        /** e.g. "sandbox" | "trongrid" -- whichever provider actually answered, reported honestly by the adapter itself. */
        String source,
        /** True if observedAt is null or older than the platform's freshness window (currently 30s). */
        boolean stale,
        OffsetDateTime nextRefreshAllowedAt,
        /** True if a refreshBalance call was suppressed by the freshness window or single-flight guard -- never an error. */
        boolean refreshSuppressed,
        /** Set only when the most recent refresh attempt failed -- balance/observedAt above remain the last successfully observed values, never zeroed out. */
        String refreshFailureReason) {
}
