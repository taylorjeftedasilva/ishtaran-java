package com.ishtaran.sdk.model.dataplane;

import java.util.UUID;

public record StateResponse(UUID stateId, String name, boolean isInitial, boolean isFinal) {
}
