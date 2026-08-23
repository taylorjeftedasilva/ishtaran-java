package com.ishtaran.sdk.model.dataplane;

import java.util.UUID;

/** DEC-032/BR-HLD-006. */
public record ClaimAccountHolderInvitationResult(
        boolean success,
        UUID relationshipId,
        String errorCode) {
}
