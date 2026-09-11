package com.ishtaran.sdk.model.dataplane;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

/**
 * A single Asset's balance aggregated across the AssetNetworks the caller asked about — never
 * summed across different Assets (e.g. USDT never added to ETH).
 */
public record WalletAssetBalanceResponse(
        UUID assetId,
        String assetSymbol,
        BigDecimal aggregateBalance,
        List<WalletNetworkBalanceResponse> networkBalances) {
}
