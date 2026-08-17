package com.ishtaran.sdk.model.dataplane;

import java.util.UUID;

public record TransitionResponse(UUID transitionId, UUID fromStateId, UUID toStateId) {
}
