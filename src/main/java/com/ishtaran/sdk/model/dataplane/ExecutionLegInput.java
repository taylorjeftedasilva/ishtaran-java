package com.ishtaran.sdk.model.dataplane;

import java.math.BigDecimal;

/** A leg already computed by the caller (Settlement/Withdrawal, DEC-025) — never recalculated by the SDK. */
public record ExecutionLegInput(String role, String destinationAddress, BigDecimal amount) {
}
