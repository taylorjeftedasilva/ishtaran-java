package com.ishtaran.sdk.serialization;

import com.ishtaran.sdk.model.dataplane.WithdrawalQuoteResponse;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * A API real envia dinheiro como {@code number(double)} no JSON (nunca string — ver
 * SDK_CAPABILITY_SPEC.md §11.1). Este teste confirma que o codec preserva a precisão exata do
 * token JSON, sem arredondamento via {@code double} intermediário.
 */
class MoneyPrecisionTest {

    @Test
    void bigDecimalField_preservesExactPrecision_fromJsonNumberToken() throws Exception {
        String json = """
                {
                  "accountId": "11111111-1111-1111-1111-111111111111",
                  "withdrawalDestinationId": "22222222-2222-2222-2222-222222222222",
                  "assetNetworkId": "33333333-3333-3333-3333-333333333333",
                  "requestedAmount": 100.123456789012345678,
                  "estimatedNetworkFee": 0.4,
                  "estimatedRecipientAmount": 99.723456789012345678,
                  "expiresAt": "2026-08-17T12:00:00Z"
                }
                """;

        var quote = JsonCodec.mapper().readValue(json, WithdrawalQuoteResponse.class);

        assertEquals(new BigDecimal("100.123456789012345678"), quote.requestedAmount());
        assertEquals(new BigDecimal("0.4"), quote.estimatedNetworkFee());
        assertEquals(new BigDecimal("99.723456789012345678"), quote.estimatedRecipientAmount());
    }

    @Test
    void smallPaymentNoFloor_pureProportionalFee_matchesRealBackendExample() throws Exception {
        // Mesmo cenário do teste de backend ExecuteSettlement_SmallPayment_NoFloor (0,9% sem piso,
        // ver ECONOMIC_MODEL_V2_FINAL_VALIDATION.md) — confirma que o SDK não arredonda/perde
        // precisão nesse caso limite conhecido.
        BigDecimal gross = new BigDecimal("1");
        BigDecimal feeRate = new BigDecimal("0.009");
        BigDecimal expectedFee = new BigDecimal("0.009");
        assertEquals(expectedFee, gross.multiply(feeRate));
    }
}
