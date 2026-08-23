package com.ishtaran.sdk.signing;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * SPEC-019/brief §10 ("Criar deterministic test vectors compartilhados entre as 4 linguagens") —
 * proves this SDK's {@link CanonicalHash} implementation reproduces the backend's C# reference
 * hashes byte for byte. Inputs and expected hashes come from
 * {@code docs/specs/execution-custody/CANONICAL-HASH-TEST-VECTORS.md} — the single source of
 * truth. If any of these fail, this is a cross-language paridade bug, never an "acceptable
 * variation" — never adjust the expected value to make a test pass; fix the algorithm instead.
 */
class CanonicalHashReferenceVectorTest {

    private static final UUID ENVIRONMENT_ID = UUID.fromString("11111111-1111-1111-1111-111111111111");
    private static final UUID WALLET_ID = UUID.fromString("22222222-2222-2222-2222-222222222222");
    private static final UUID ASSET_NETWORK_ID = UUID.fromString("33333333-3333-3333-3333-333333333333");
    private static final String SOURCE_ADDRESS = "TSourceAddress1234567890123456";
    private static final Instant EXPIRES_AT = Instant.parse("2026-08-22T12:15:00Z");

    @Test
    void vector1_sellerLeg_matchesReferenceHash() {
        var hash = CanonicalHash.compute(
                1, ENVIRONMENT_ID, WALLET_ID, 5, "settlement:44444444-4444-4444-4444-444444444444",
                ASSET_NETWORK_ID, SOURCE_ADDRESS, "Seller", "TSellerDestinationAddress123456",
                new BigDecimal("90"), EXPIRES_AT);

        assertEquals("4623D19A6CFA8B7D7EA9D53F2E09DD5D98C0B237F980182CE3D74B3D9385CEA7", hash);
    }

    @Test
    void vector2_platformFeeLeg_matchesReferenceHash() {
        var hash = CanonicalHash.compute(
                1, ENVIRONMENT_ID, WALLET_ID, 5, "settlement:44444444-4444-4444-4444-444444444444",
                ASSET_NETWORK_ID, SOURCE_ADDRESS, "PlatformFee", "TIshtaranFeeDestinationAddr123",
                new BigDecimal("1"), EXPIRES_AT);

        assertEquals("B11A474993D19ED9D0F97B657134A76931626BD52F6082879395AC54EEF8063B", hash);
    }

    @Test
    void vector3_withdrawalLeg_matchesReferenceHash() {
        var hash = CanonicalHash.compute(
                1, ENVIRONMENT_ID, WALLET_ID, 12, "withdrawal:55555555-5555-5555-5555-555555555555",
                ASSET_NETWORK_ID, SOURCE_ADDRESS, "Withdrawal", "TWithdrawalDestinationAddr1234",
                new BigDecimal("250.5"), EXPIRES_AT);

        assertEquals("F297DBED71AA6646D93F489D9B4C2891779D440BC43A39D1820074358AE4F9EA", hash);
    }

    @Test
    void vector4_tamperedAmount_producesDifferentHash_neverTheOriginal() {
        var hash = CanonicalHash.compute(
                1, ENVIRONMENT_ID, WALLET_ID, 5, "settlement:44444444-4444-4444-4444-444444444444",
                ASSET_NETWORK_ID, SOURCE_ADDRESS, "Seller", "TSellerDestinationAddress123456",
                new BigDecimal("90.000000000000000001"), EXPIRES_AT);

        assertEquals("4FFA1C26FC90EAFEE081822F4E21AF2DEEA7235F07091DB7CD4C805446770792", hash);
    }
}
