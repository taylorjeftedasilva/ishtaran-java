package com.ishtaran.sdk.model.dataplane;

import java.util.UUID;

public record StateInput(UUID id, String name, boolean isInitial, boolean isFinal) {
}
