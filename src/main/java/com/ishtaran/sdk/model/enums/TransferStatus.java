package com.ishtaran.sdk.model.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

/**
 * PROMPT 7/7.1 (SPEC-TRANSFER-001/002, BR-TRF-008) — mirrors Transfers.Contracts.Enums.TransferStatus.
 * Group B — raw integer in the JSON, same pattern as {@link SettlementStatus}. Real lifecycle:
 * CREATED -&gt; AWAITING_SIGNATURE (a real SigningRequest was created, the client still has to sign
 * each Leg locally) -&gt; CONFIRMED (all Legs confirmed on-chain) / FAILED. SUBMITTED is catalogued
 * but never actually reached — ExecutionLegStatus already tracks that, same as Withdrawal under
 * SelfCustody.
 */
public final class TransferStatus {

    public static final TransferStatus CREATED = new TransferStatus("CREATED", 0);
    public static final TransferStatus AWAITING_SIGNATURE = new TransferStatus("AWAITING_SIGNATURE", 1);
    public static final TransferStatus SUBMITTED = new TransferStatus("SUBMITTED", 2);
    public static final TransferStatus CONFIRMED = new TransferStatus("CONFIRMED", 3);
    public static final TransferStatus FAILED = new TransferStatus("FAILED", 4);

    private static final Map<Integer, TransferStatus> KNOWN = new ConcurrentHashMap<>();

    static {
        for (var v : new TransferStatus[] {CREATED, AWAITING_SIGNATURE, SUBMITTED, CONFIRMED, FAILED}) {
            KNOWN.put(v.rawValue, v);
        }
    }

    private final String name;
    private final int rawValue;

    private TransferStatus(String name, int rawValue) {
        this.name = name;
        this.rawValue = rawValue;
    }

    @JsonCreator
    public static TransferStatus fromRaw(int raw) {
        return KNOWN.getOrDefault(raw, new TransferStatus("UNKNOWN", raw));
    }

    @JsonValue
    public int rawValue() {
        return rawValue;
    }

    public String name() {
        return name;
    }

    public boolean isUnknown() {
        return !KNOWN.containsKey(rawValue);
    }

    @Override
    public boolean equals(Object o) {
        return o instanceof TransferStatus other && Objects.equals(other.rawValue, this.rawValue);
    }

    @Override
    public int hashCode() {
        return Objects.hash(rawValue);
    }

    @Override
    public String toString() {
        return name + "(" + rawValue + ")";
    }
}
