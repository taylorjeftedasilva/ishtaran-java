package com.ishtaran.sdk.model.dataplane;

import com.ishtaran.sdk.model.enums.NetworkOperationKind;

import java.math.BigDecimal;

/** A single physical operation to be priced — input to {@code NetworkExecutionResource.quote()}, never interpreted by the caller. */
public record NetworkExecutionOperationInput(String destinationAddress, BigDecimal amount, NetworkOperationKind kind, String reference) {
}
