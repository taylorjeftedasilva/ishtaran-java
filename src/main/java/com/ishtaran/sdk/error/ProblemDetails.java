package com.ishtaran.sdk.error;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * Real error body (RFC7807 + {@code code} extension) — see SDK_CAPABILITY_SPEC.md §6.1/§6.2.
 * Never used for 401/403 (no body, see §6.3).
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public record ProblemDetails(String type, String title, Integer status, String detail, String code) {
}
