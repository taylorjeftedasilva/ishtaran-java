package com.ishtaran.sdk.model.dataplane;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

/**
 * DEC-032 — explicit link (never ownership) between an Organization and an {@code AccountHolder}/
 * {@code Account}. {@code authorizedApplicationIds} is scoped to THIS Relationship — it never
 * leaks to another Organization related to the same AccountHolder. Returned by {@code AccountsResource#list}.
 */
public record OrganizationAccountResponse(
        UUID relationshipId,
        UUID accountId,
        UUID accountHolderId,
        String externalId,
        String relationshipStatus,
        String accountStatus,
        OffsetDateTime createdAt,
        List<UUID> authorizedApplicationIds) {
}
