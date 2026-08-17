package com.ishtaran.sdk.model.dataplane;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

/** Espelha {@code Accounts.Contracts.Responses.AccountResponse} exatamente. */
public record AccountResponse(
        UUID accountId,
        UUID organizationId,
        String externalId,
        String status,
        OffsetDateTime createdAt,
        List<UUID> authorizedApplicationIds) {
}
