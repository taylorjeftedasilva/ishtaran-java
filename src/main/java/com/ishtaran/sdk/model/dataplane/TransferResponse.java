package com.ishtaran.sdk.model.dataplane;

import com.ishtaran.sdk.model.enums.TransferStatus;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;

public record TransferResponse(
        UUID transferId,
        UUID organizationId,
        UUID applicationId,
        UUID environmentId,
        UUID sourceAccountId,
        UUID assetNetworkId,
        BigDecimal amount,
        String destinationAddress,
        UUID destinationAccountId,
        BigDecimal platformFeeAmount,
        BigDecimal platformFeePercentage,
        TransferStatus status,
        /**
         * BR-TRF-008 — the real SigningRequest (ExecutionCustody). Fetch it via
         * {@code client.signingRequests().get(signingRequestId)} to sign locally. Null only if the
         * Transfer failed before a SigningRequest could be created.
         */
        UUID signingRequestId,
        OffsetDateTime createdAt,
        OffsetDateTime confirmedAt,
        String failureReason) {
}
