package com.ishtaran.sdk.util;

/** Formato fixo {@code ishtaran-java/<version>} — nunca contém dado pessoal (regra do brief). */
public final class UserAgent {

    public static final String SDK_VERSION = "1.0.0-SNAPSHOT";
    public static final String DEFAULT = "ishtaran-java/" + SDK_VERSION;

    private UserAgent() {
    }
}
