package com.ishtaran.sdk.model.dataplane;

import java.time.OffsetDateTime;
import java.util.UUID;

/**
 * DEC-032/BR-HLD-005. {@code plainTextToken} exists only in this response, a single time — treat
 * it as a secret (never log it, deliver it to the holder through a secure channel outside the API).
 */
public record CreateAccountHolderInvitationResult(
        UUID invitationId,
        String plainTextToken,
        OffsetDateTime expiresAt) {
}
