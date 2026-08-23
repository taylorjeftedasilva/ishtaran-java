package com.ishtaran.sdk.model.dataplane;

import java.util.UUID;

/** DEC-032/BR-HLD-005/006 -- "new AccountHolder" path: atomic signup + claim. */
public record SignUpAndClaimAccountHolderInvitationResult(
        boolean success,
        UUID relationshipId,
        AccountHolderTokenResult token,
        String errorCode) {
}
