package com.ishtaran.sdk.model.dataplane;

/** BR-WLT-002 -- the only legitimate point of exposure for derivation material (Confidential, never Secret). */
public record WalletPublicMaterialResult(String publicDerivationMaterial) {
}
