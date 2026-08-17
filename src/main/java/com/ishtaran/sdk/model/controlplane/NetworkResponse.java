package com.ishtaran.sdk.model.controlplane;

import java.time.OffsetDateTime;
import java.util.UUID;

public record NetworkResponse(UUID networkId, String code, String name, Integer chainId, boolean isTestnet, String status, OffsetDateTime createdAt) {
}
