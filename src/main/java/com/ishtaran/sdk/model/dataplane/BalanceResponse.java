package com.ishtaran.sdk.model.dataplane;

import java.math.BigDecimal;

/**
 * Mirrors {@code Ledger.Contracts.Responses.BalanceResponse} — does not appear in the generated
 * OpenAPI (the real route uses {@code Results.Ok(...)} without {@code .Produces<T>()}, so
 * Swashbuckle cannot infer the schema; extracted directly from the source code, never invented).
 */
public record BalanceResponse(BigDecimal available, BigDecimal pending, BigDecimal reserved) {
}
