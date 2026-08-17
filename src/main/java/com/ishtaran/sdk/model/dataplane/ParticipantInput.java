package com.ishtaran.sdk.model.dataplane;

import java.math.BigDecimal;
import java.util.UUID;

public record ParticipantInput(UUID accountId, String role, boolean isPayer, BigDecimal splitPercentage) {
}
