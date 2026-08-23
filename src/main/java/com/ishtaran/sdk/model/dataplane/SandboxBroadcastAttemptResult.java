package com.ishtaran.sdk.model.dataplane;

import java.util.UUID;

/** Returned by {@code simulateWithdrawal} -- real anonymous object, see SandboxEndpoints.cs. */
public record SandboxBroadcastAttemptResult(UUID sandboxBroadcastAttemptId) {
}
