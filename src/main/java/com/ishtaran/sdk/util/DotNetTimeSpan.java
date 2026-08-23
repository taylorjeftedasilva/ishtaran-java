package com.ishtaran.sdk.util;

import java.time.Duration;

/**
 * {@code RotateApiKeyRequest.OverlapWindow} is a real .NET {@code TimeSpan} (no custom converter
 * registered -- confirmed via grep in `OrganizationTenancy.Contracts` -- so it uses
 * {@code System.Text.Json}'s native support since .NET 6, constant format "c":
 * {@code [-][d.]hh:mm:ss[.fffffff]}). Never use the ISO-8601 "PT..." format from
 * {@code java.time.Duration.toString()} here -- it would break the real call.
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
