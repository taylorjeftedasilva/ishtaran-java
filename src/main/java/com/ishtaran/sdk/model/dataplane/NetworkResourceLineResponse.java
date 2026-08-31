package com.ishtaran.sdk.model.dataplane;

import java.math.BigDecimal;

/** SPEC-NETEXEC-001 Descoberta 6/BR-NET-008 — {@code resourceCode} is opaque (string), never interpreted by the generic caller. */
public record NetworkResourceLineResponse(String resourceCode, BigDecimal quantity, String unit) {
}
