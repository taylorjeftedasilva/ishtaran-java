package com.ishtaran.sdk.model.controlplane;

import java.time.OffsetDateTime;

/** Mirrors {@code IdentityAccess.Contracts.Responses.TokenResult} exactly. */
public record TokenResult(
        boolean success,
        String accessToken,
        String refreshToken,
        OffsetDateTime expiresAt,
        String errorCode) {
}
