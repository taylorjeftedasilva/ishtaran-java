package com.ishtaran.sdk.model.dataplane;

import java.util.UUID;

/**
 * {@code verified=false} corresponds to the public code {@code SIGNED_TRANSACTION_MISMATCH}
 * (backend SPEC-020 section Errors) -- never broadcast (INV-SC-03). {@code allLegsVerified=true}
 * means the broadcast of ALL Legs has already been triggered in the same Command (all-signatures gate).
 */
public record SubmitSignedTransactionResult(UUID executionLegId, boolean verified, String mismatchReason, boolean allLegsVerified) {
}
