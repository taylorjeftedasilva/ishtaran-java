package com.ishtaran.sdk.model.dataplane;

import java.time.OffsetDateTime;
import java.util.UUID;

/**
 * Mirrors {@code Accounts.Contracts.Responses.AccountResponse} exactly. DEC-032 — Account no
 * longer belongs directly to a single Organization (global identity, linked to N Organizations
 * via Relationship). For the Organization-scoped link, see {@link OrganizationAccountResponse},
 * returned by {@code AccountsResource#list}.
 */
public record AccountResponse(
        UUID accountId,
        UUID accountHolderId,
        String status,
        OffsetDateTime createdAt) {
}
