package com.ishtaran.sdk.model.dataplane;

import java.time.OffsetDateTime;

/**
 * Mirrors {@code Accounts.Contracts.Responses.TokenResult} exactly (DEC-032). Its own type —
 * never {@code com.ishtaran.sdk.model.controlplane.TokenResult} (Member): the AccountHolder has no
 * Refresh Token in this version (MVP, deliberately reduced scope on the backend) and never carries
 * {@code organizationId}/{@code Role}.
 */
public record AccountHolderTokenResult(
        boolean success,
        String accessToken,
        OffsetDateTime expiresAt,
        String errorCode) {
}
