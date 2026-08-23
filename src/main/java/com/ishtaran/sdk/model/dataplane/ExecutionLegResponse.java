package com.ishtaran.sdk.model.dataplane;

import java.math.BigDecimal;
import java.util.UUID;

/**
 * {@code status}/{@code mismatchReason}/{@code broadcastReference} are raw strings in the real JSON
 * (the backend already converts them to text in the Mapping layer, Group A — see
 * {@code SDK_CAPABILITY_SPEC.md §11.3}), never a typed enum in this version — the possible values
 * ({@code PendingSignature}/{@code Verified}/{@code MismatchDetected}/{@code Broadcast}/...) still
 * have no closed catalog documented outside the backend's source code.
 */
public record ExecutionLegResponse(
        UUID executionLegId,
        String role,
        String destinationAddress,
        BigDecimal amount,
        String canonicalHash,
        String status,
        String mismatchReason,
        String broadcastReference) {
}
