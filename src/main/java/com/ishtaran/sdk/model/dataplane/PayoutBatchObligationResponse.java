package com.ishtaran.sdk.model.dataplane;

import com.ishtaran.sdk.model.enums.PayoutBatchObligationStatus;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

public record PayoutBatchObligationResponse(
        UUID ownerId,
        BigDecimal amount,
        List<PayoutBatchSourceObligationResponse> sourceObligations,
        String destinationAddress,
        PayoutBatchObligationStatus status) {
}
