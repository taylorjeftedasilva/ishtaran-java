package com.ishtaran.sdk.model.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Grupo B — inteiro bruto no JSON (sem JsonStringEnumConverter no backend) (ver SDK_CAPABILITY_SPEC.md §11.3). Classe (não {@code enum} Java) para permitir
 * que um valor desconhecido preserve o int bruto exato recebido em vez de colapsar em uma
 * constante fixa sem informação (§11.4 — forward-compatibility real).
 */
public final class WebhookDeliveryStatus {

    public static final WebhookDeliveryStatus PENDING = new WebhookDeliveryStatus("PENDING", 0);
    public static final WebhookDeliveryStatus DELIVERING = new WebhookDeliveryStatus("DELIVERING", 1);
    public static final WebhookDeliveryStatus DELIVERED = new WebhookDeliveryStatus("DELIVERED", 2);
    public static final WebhookDeliveryStatus RETRYING = new WebhookDeliveryStatus("RETRYING", 3);
    public static final WebhookDeliveryStatus DEAD_LETTER = new WebhookDeliveryStatus("DEAD_LETTER", 4);
    public static final WebhookDeliveryStatus CANCELLED = new WebhookDeliveryStatus("CANCELLED", 5);

    private static final Map<Integer, WebhookDeliveryStatus> KNOWN = new ConcurrentHashMap<>();

    static {
        for (var v : new WebhookDeliveryStatus[] {PENDING, DELIVERING, DELIVERED, RETRYING, DEAD_LETTER, CANCELLED}) {
            KNOWN.put(v.rawValue, v);
        }
    }

    private final String name;
    private final int rawValue;

    private WebhookDeliveryStatus(String name, int rawValue) {
        this.name = name;
        this.rawValue = rawValue;
    }

    @JsonCreator
    public static WebhookDeliveryStatus fromRaw(int raw) {
        return KNOWN.getOrDefault(raw, new WebhookDeliveryStatus("UNKNOWN", raw));
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
        return o instanceof WebhookDeliveryStatus other && Objects.equals(other.rawValue, this.rawValue);
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
