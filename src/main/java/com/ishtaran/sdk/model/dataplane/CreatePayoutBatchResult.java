package com.ishtaran.sdk.model.dataplane;

import java.util.UUID;

/** {@code payoutBatchId} is {@code null} when there were no eligible candidates (204 No Content, a legitimate no-op — never an error). */
public record CreatePayoutBatchResult(UUID payoutBatchId) {
}
