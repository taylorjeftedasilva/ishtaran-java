package com.ishtaran.sdk.model.controlplane;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;

public record AssetNetworkResponse(
        UUID assetNetworkId,
        UUID assetId,
        UUID networkId,
        int minConfirmations,
        BigDecimal minAmount,
        BigDecimal maxAmount,
        BigDecimal estimatedFee,
        String contractAddress,
        String status,
        OffsetDateTime createdAt) {
}
