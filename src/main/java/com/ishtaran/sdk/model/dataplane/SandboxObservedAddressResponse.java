package com.ishtaran.sdk.model.dataplane;

import java.time.OffsetDateTime;
import java.util.UUID;

public record SandboxObservedAddressResponse(
        UUID sandboxObservedAddressId, String address, UUID assetNetworkId,
        String lastObservedReference, Integer lastConfirmationCount, OffsetDateTime createdAt) {
}
