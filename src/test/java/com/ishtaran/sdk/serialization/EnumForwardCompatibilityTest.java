package com.ishtaran.sdk.serialization;

import com.ishtaran.sdk.model.enums.AccountStatus;
import com.ishtaran.sdk.model.enums.WithdrawalStatus;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * See SDK_CAPABILITY_SPEC.md §11.4 — no enum may throw a deserialization exception for an
 * unrecognized value; it must fall back to UNKNOWN while preserving the exact raw value.
 */
class EnumForwardCompatibilityTest {

    @Test
    void groupB_knownIntValue_deserializesToNamedConstant() throws Exception {
        var status = JsonCodec.mapper().readValue("8", WithdrawalStatus.class);
        assertEquals(WithdrawalStatus.COMPLETED, status);
        assertFalse(status.isUnknown());
    }

    @Test
    void groupB_unknownIntValue_neverThrows_fallsBackToUnknownPreservingRawValue() throws Exception {
        var status = JsonCodec.mapper().readValue("99", WithdrawalStatus.class);
        assertTrue(status.isUnknown());
        assertEquals(99, status.rawValue());
    }

    @Test
    void groupA_knownStringValue_deserializesToNamedConstant() throws Exception {
        var status = JsonCodec.mapper().readValue("\"Frozen\"", AccountStatus.class);
        assertEquals(AccountStatus.FROZEN, status);
        assertFalse(status.isUnknown());
    }

    @Test
    void groupA_unknownStringValue_neverThrows_fallsBackToUnknownPreservingRawValue() throws Exception {
        var status = JsonCodec.mapper().readValue("\"SomeFutureStatus\"", AccountStatus.class);
        assertTrue(status.isUnknown());
        assertEquals("SomeFutureStatus", status.rawValue());
    }

    @Test
    void equality_isByRawValue_notByReference() {
        assertEquals(WithdrawalStatus.fromRaw(8), WithdrawalStatus.fromRaw(8));
    }
}
