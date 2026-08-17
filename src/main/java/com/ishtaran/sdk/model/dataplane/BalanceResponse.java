package com.ishtaran.sdk.model.dataplane;

import java.math.BigDecimal;

/**
 * Espelha {@code Ledger.Contracts.Responses.BalanceResponse} — não aparece no OpenAPI gerado (a
 * rota real usa {@code Results.Ok(...)} sem {@code .Produces<T>()}, então o Swashbuckle não infere
 * o schema; extraído diretamente do código-fonte, nunca inventado).
 */
public record BalanceResponse(BigDecimal available, BigDecimal pending, BigDecimal reserved) {
}
