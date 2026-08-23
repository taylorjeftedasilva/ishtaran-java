package com.ishtaran.sdk.model.dataplane;

import java.util.UUID;

/** BR-WLT-001 — {@code derivationReference} is never reused across calls (each one allocates a new index). */
public record AllocatedDepositAddressResult(UUID walletId, String address, long derivationReference) {
}
