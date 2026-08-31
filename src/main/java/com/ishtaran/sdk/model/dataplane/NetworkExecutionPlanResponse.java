package com.ishtaran.sdk.model.dataplane;

import java.util.List;
import java.util.UUID;

/** SPEC-NETEXEC-001 BL-NET-002 — structured result of {@code INetworkExecutionPlanner.Plan(...)}, never flattened into loose fields. */
public record NetworkExecutionPlanResponse(UUID assetNetworkId, List<NetworkExecutionTransactionResponse> transactions) {
}
