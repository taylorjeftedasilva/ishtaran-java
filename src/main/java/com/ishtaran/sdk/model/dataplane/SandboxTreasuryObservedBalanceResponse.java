package com.ishtaran.sdk.model.dataplane;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;

public record SandboxTreasuryObservedBalanceResponse(UUID assetNetworkId, BigDecimal balance, OffsetDateTime updatedAt) {
}
