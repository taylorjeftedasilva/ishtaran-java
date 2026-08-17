package com.ishtaran.sdk.model.dataplane;

import com.ishtaran.sdk.model.enums.EntryNature;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;

/** Espelha {@code Ledger.Contracts.Responses.LedgerEntryResponse} exatamente (ver nota em {@link BalanceResponse}). */
public record LedgerEntryResponse(
        UUID entryId,
        UUID ledgerAccountId,
        UUID entryGroupId,
        EntryNature nature,
        BigDecimal amount,
        String originReference,
        UUID reversalOfEntryGroupId,
        OffsetDateTime createdAt) {
}
