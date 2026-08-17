package com.ishtaran.sdk.model.dataplane;

import java.util.UUID;

public record TransitionInput(UUID id, UUID fromStateId, UUID toStateId) {
}
