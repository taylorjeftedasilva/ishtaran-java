package com.ishtaran.sdk.util;

/**
 * Mascaramento central de segredos para log/toString/mensagens de exceção — nunca o valor bruto.
 * Formato genérico (4 primeiros + **** + 4 últimos): a API Key real do Ishtaran não tem prefixo de
 * ambiente (ApiKeyGenerator.Generate() é Base64 puro de 32 bytes — ver SDK_CAPABILITY_SPEC.md
 * §12.5), então este SDK nunca assume um prefixo tipo {@code sk_live_} que não existe de verdade.
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

    /** Headers cujo valor nunca deve aparecer em log, mesmo com logging opt-in habilitado. */
    public static boolean isSensitiveHeader(String headerName) {
        if (headerName == null) {
            return false;
        }
        var lower = headerName.toLowerCase(java.util.Locale.ROOT);
        return lower.equals("authorization") || lower.equals("x-api-key");
    }
}
