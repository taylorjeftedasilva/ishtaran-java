package com.ishtaran.sdk.model.dataplane;

import java.util.UUID;

public record EventIngestionResult(UUID eventId, String outcome, String rejectionReason,
                                    UUID fromStateId, UUID toStateId, UUID ruleId) {
}
