package com.ishtaran.sdk.model.dataplane;

import java.math.BigDecimal;

/** SPEC-NETEXEC-001 Descoberta 2 — the physical unit (what will be one real on-chain transaction), grouping 1..N transfers. */
public record NetworkExecutionTransferResponse(String destinationAddress, BigDecimal amount, String sourceOperationReference) {
}
