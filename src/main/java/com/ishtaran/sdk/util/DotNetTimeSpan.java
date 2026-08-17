package com.ishtaran.sdk.util;

import java.time.Duration;

/**
 * {@code RotateApiKeyRequest.OverlapWindow} é um {@code TimeSpan} real do .NET (nenhum converter
 * customizado registrado — confirmado via grep em `OrganizationTenancy.Contracts` — então usa o
 * suporte nativo do {@code System.Text.Json} desde .NET 6, formato constante "c":
 * {@code [-][d.]hh:mm:ss[.fffffff]}). Nunca usar o formato ISO-8601 "PT..." de
 * {@code java.time.Duration.toString()} aqui — quebraria a chamada real.
 */
public final class DotNetTimeSpan {

    private DotNetTimeSpan() {
    }

    public static String format(Duration duration) {
        long totalSeconds = duration.getSeconds();
        long days = totalSeconds / 86400;
        long hours = (totalSeconds % 86400) / 3600;
        long minutes = (totalSeconds % 3600) / 60;
        long seconds = totalSeconds % 60;
        int nanos = duration.getNano();

        var sb = new StringBuilder();
        if (days != 0) {
            sb.append(days).append('.');
        }
        sb.append(String.format("%02d:%02d:%02d", hours, minutes, seconds));
        if (nanos != 0) {
            sb.append('.').append(String.format("%07d", nanos / 100));
        }
        return sb.toString();
    }
}
