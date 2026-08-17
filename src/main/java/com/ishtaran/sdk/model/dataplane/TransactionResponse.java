package com.ishtaran.sdk.model.dataplane;

import com.ishtaran.sdk.model.enums.TransactionStatus;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

public record TransactionResponse(
        UUID transactionId,
        UUID organizationId,
        UUID applicationId,
        UUID workflowVersionId,
        UUID currentWorkflowStateId,
        UUID assetNetworkId,
        BigDecimal amount,
        TransactionStatus status,
        UUID payerAccountId,
        List<ParticipantResponse> participants,
        OffsetDateTime createdAt,
        BigDecimal settledAmount,
        BigDecimal refundedAmount) {
}
