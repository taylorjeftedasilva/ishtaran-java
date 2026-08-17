package com.ishtaran.sdk.model.dataplane;

import com.ishtaran.sdk.model.enums.PaymentIntentStatus;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

public record PaymentIntentResponse(
        UUID paymentIntentId,
        UUID organizationId,
        UUID transactionId,
        UUID assetNetworkId,
        BigDecimal amount,
        PaymentIntentStatus status,
        OffsetDateTime expiresAt,
        String depositAddress,
        List<DepositResponse> deposits,
        OffsetDateTime createdAt) {
}
