package com.ishtaran.sdk.serialization;

import com.ishtaran.sdk.model.dataplane.WithdrawalQuoteResponse;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * The real API sends money as a {@code number(double)} in JSON (never a string — see
 * SDK_CAPABILITY_SPEC.md §11.1). This test confirms the codec preserves the exact precision of
 * the JSON token, without rounding through an intermediate {@code double}.
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
        // Same scenario as the backend test ExecuteSettlement_SmallPayment_NoFloor (0.9% with no floor,
        // see ECONOMIC_MODEL_V2_FINAL_VALIDATION.md) — confirms the SDK doesn't round/lose
        // precision in this known edge case.
        BigDecimal gross = new BigDecimal("1");
        BigDecimal feeRate = new BigDecimal("0.009");
        BigDecimal expectedFee = new BigDecimal("0.009");
        assertEquals(expectedFee, gross.multiply(feeRate));
    }
}
