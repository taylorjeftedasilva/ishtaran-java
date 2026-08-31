package com.ishtaran.sdk.model.dataplane;

import java.util.List;

public record NetworkExecutionTransactionResponse(List<NetworkExecutionTransferResponse> transfers) {
}
