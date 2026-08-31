package com.ishtaran.sdk.model.dataplane;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;

/** SPEC-025 Descoberta 6/7 — always the frozen copy captured at reservation time, never recomputed/reread on every read. */
public record NetworkExecutionQuoteSnapshotResponse(
        String network,
        BigDecimal nativeExecutionCost,
        UUID resourceAssetNetworkId,
        String quoteCurrency,
        BigDecimal fx,
        BigDecimal totalCharged,
        BigDecimal authorizedNativeCost,
        OffsetDateTime expiresAt) {
}
