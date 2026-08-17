package com.ishtaran.sdk.model.dataplane;

import com.ishtaran.sdk.model.enums.DepositStatus;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;

public record DepositResponse(
        UUID depositId,
        UUID paymentIntentId,
        String technicalReference,
        BigDecimal amount,
        DepositStatus status,
        int confirmationCount,
        boolean wasConfirmedBeforeReorg,
        boolean isLate,
        OffsetDateTime createdAt) {
}
