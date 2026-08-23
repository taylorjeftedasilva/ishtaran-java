package com.ishtaran.sdk.model.controlplane;

import java.time.OffsetDateTime;
import java.util.UUID;

/** {@code kind}/{@code status} are real strings (Group A — see SDK_CAPABILITY_SPEC.md §11.3). */
public record AssetResponse(UUID assetId, String symbol, String name, int decimals, String kind, String status, OffsetDateTime createdAt) {
}
