package com.ishtaran.sdk.model.controlplane;

import java.time.OffsetDateTime;
import java.util.UUID;

/** Mirrors {@code IdentityAccess.Contracts.Responses.MemberResponse} exactly. */
public record MemberResponse(
        UUID memberId,
        UUID organizationId,
        String email,
        String role,
        String status,
        OffsetDateTime createdAt,
        OffsetDateTime activatedAt,
        OffsetDateTime lastLoginAt) {
}
