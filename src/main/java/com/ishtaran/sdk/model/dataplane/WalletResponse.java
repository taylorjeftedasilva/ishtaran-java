package com.ishtaran.sdk.model.dataplane;

import com.ishtaran.sdk.model.enums.DerivationScheme;

import java.time.OffsetDateTime;
import java.util.UUID;

/** BR-WLT-002 -- never includes derivation material (dedicated route, {@link WalletPublicMaterialResult}). */
public record WalletResponse(
        UUID walletId,
        UUID applicationId,
        UUID networkId,
        DerivationScheme scheme,
        long nextDerivationIndex,
        OffsetDateTime registeredAt) {
}
