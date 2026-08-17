package com.ishtaran.sdk.model.controlplane;

import java.util.UUID;

/** Espelha {@code CompositionRoot.EndpointMapping.SignUpResponse} exatamente. */
public record SignUpResponse(UUID organizationId, UUID memberId, TokenResult token) {
}
