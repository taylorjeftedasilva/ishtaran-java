package com.ishtaran.sdk.wallet;

import java.util.Objects;

/**
 * Result of {@link WalletFactory#generate()} — bundles the PUBLIC {@link Wallet} (safe to
 * register with the API), the ready-to-use {@link InMemorySigner}, and the recovery mnemonic.
 * <p>
 * The mnemonic is <b>never</b> persisted or transmitted by this SDK on its own — exporting it is
 * always an explicit, local, opt-in action by the integrator (SPEC-018 brief §9: "Secret export:
 * explicit, local, opt-in. Never automatic. Never sent to the API."). Read it once, show it to the
 * end user for backup, then let this object go out of scope — nothing here retains it beyond this
 * record.
 */
public final class GeneratedWallet {

    private final Wallet wallet;
    private final InMemorySigner signer;
    private final String mnemonic;

    GeneratedWallet(Wallet wallet, InMemorySigner signer, String mnemonic) {
        this.wallet = Objects.requireNonNull(wallet);
        this.signer = Objects.requireNonNull(signer);
        this.mnemonic = Objects.requireNonNull(mnemonic);
    }

    public Wallet wallet() {
        return wallet;
    }

    public InMemorySigner signer() {
        return signer;
    }

    /** Explicit backup export point — the integrator decides if/when/how to surface this to the end user. Never logged by this class. */
    public String mnemonic() {
        return mnemonic;
    }
}
