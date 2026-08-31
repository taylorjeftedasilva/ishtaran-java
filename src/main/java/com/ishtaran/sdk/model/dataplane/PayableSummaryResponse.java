package com.ishtaran.sdk.model.dataplane;

import java.math.BigDecimal;

/**
 * SPEC-024 BL-PAY-004/BR-PAY-002 — "a receber" per beneficiary, never confused with the Account's
 * on-chain (Available) balance. {@code accrued} = sum of Payable; {@code reservedForPayout} = sum
 * of open PayoutBatches; {@code paid} = cumulative delivered history (never confused with Available).
 */
public record PayableSummaryResponse(BigDecimal accrued, BigDecimal reservedForPayout, BigDecimal paid) {
}
