package com.ishtaran.sdk.util;

/**
 * Central secret masking for log/toString/exception messages -- never the raw value.
 * Generic format (first 4 + **** + last 4): the real Ishtaran API Key has no environment prefix
 * (ApiKeyGenerator.Generate() is plain 32-byte Base64 -- see SDK_CAPABILITY_SPEC.md section
 * 12.5), so this SDK never assumes a {@code sk_live_}-style prefix that doesn't really exist.
 */
public final class Redactor {

    private Redactor() {
    }

    public static String mask(String secret) {
        if (secret == null) {
            return "null";
        }
        if (secret.length() <= 8) {
            return "****";
        }
        return secret.substring(0, 4) + "****" + secret.substring(secret.length() - 4);
    }

    /** Headers whose value must never appear in a log, even with opt-in logging enabled. */
    public static boolean isSensitiveHeader(String headerName) {
        if (headerName == null) {
            return false;
        }
        var lower = headerName.toLowerCase(java.util.Locale.ROOT);
        return lower.equals("authorization") || lower.equals("x-api-key");
    }
}
