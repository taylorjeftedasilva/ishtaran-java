package com.ishtaran.sdk.model.dataplane;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

public record SigningRequestResponse(
        UUID signingRequestId,
        UUID applicationId,
        UUID environmentId,
        UUID networkId,
        UUID walletId,
        long derivationReference,
        String originReference,
        UUID assetNetworkId,
        String sourceAddress,
        int protocolVersion,
        List<ExecutionLegResponse> legs,
        OffsetDateTime createdAt,
        OffsetDateTime expiresAt,
        boolean isExpired) {
}
