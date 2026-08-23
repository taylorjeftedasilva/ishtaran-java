package com.ishtaran.sdk.model.dataplane;

import java.util.UUID;

/** Returned by {@code faucet}/{@code simulateDeposit} -- real anonymous object, see SandboxEndpoints.cs. */
public record SandboxObservedAddressResult(UUID sandboxObservedAddressId) {
}
