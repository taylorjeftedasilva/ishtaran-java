package com.ishtaran.sdk.model.dataplane;

import com.ishtaran.sdk.model.enums.TransactionStatus;

import java.util.UUID;

public record TransactionStateResponse(TransactionStatus status, UUID workflowVersionId, UUID currentWorkflowStateId) {
}
