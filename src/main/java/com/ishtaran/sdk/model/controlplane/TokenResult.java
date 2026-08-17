package com.ishtaran.sdk.model.controlplane;

import java.time.OffsetDateTime;

/** Espelha {@code IdentityAccess.Contracts.Responses.TokenResult} exatamente. */
public record TokenResult(
        boolean success,
        String accessToken,
        String refreshToken,
        OffsetDateTime expiresAt,
        String errorCode) {
}
