package com.ishtaran.sdk.model.dataplane;

import java.time.OffsetDateTime;
import java.util.UUID;

/** Mirrors {@code Accounts.Contracts.Responses.AccountHolderResponse} exactly (DEC-032). */
public record AccountHolderResponse(
        UUID accountHolderId,
        UUID accountId,
        String email,
        boolean hasCredentials,
        String status,
        OffsetDateTime createdAt,
        OffsetDateTime lastLoginAt) {
}
