package com.ishtaran.sdk.model.dataplane;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;

/** One AssetNetwork's contribution to a {@link WalletAssetBalanceResponse}. */
public record WalletNetworkBalanceResponse(
        UUID assetNetworkId,
        String networkCode,
        BigDecimal balance,
        OffsetDateTime observedAt,
        boolean stale) {
}
