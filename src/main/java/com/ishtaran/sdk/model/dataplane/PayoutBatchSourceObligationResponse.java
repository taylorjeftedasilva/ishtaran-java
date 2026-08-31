package com.ishtaran.sdk.model.dataplane;

import java.math.BigDecimal;

public record PayoutBatchSourceObligationResponse(String originReference, BigDecimal amount) {
}
