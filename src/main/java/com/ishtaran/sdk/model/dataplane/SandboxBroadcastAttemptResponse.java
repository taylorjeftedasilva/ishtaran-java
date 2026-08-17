package com.ishtaran.sdk.model.dataplane;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;

public record SandboxBroadcastAttemptResponse(
        UUID sandboxBroadcastAttemptId, String destinationAddress, BigDecimal amount, UUID assetNetworkId,
        String status, String technicalReference, OffsetDateTime createdAt) {
}
