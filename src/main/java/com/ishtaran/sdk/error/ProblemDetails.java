package com.ishtaran.sdk.error;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * Corpo real de erro (RFC7807 + extensão {@code code}) — ver SDK_CAPABILITY_SPEC.md §6.1/§6.2.
 * Nunca usado para 401/403 (sem corpo, ver §6.3).
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public record ProblemDetails(String type, String title, Integer status, String detail, String code) {
}
