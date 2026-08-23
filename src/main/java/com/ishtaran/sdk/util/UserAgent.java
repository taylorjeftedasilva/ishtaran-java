package com.ishtaran.sdk.util;

/** Fixed format {@code ishtaran-java/<version>} -- never contains personal data (rule from the brief). */
public final class UserAgent {

    public static final String SDK_VERSION = "1.0.0-SNAPSHOT";
    public static final String DEFAULT = "ishtaran-java/" + SDK_VERSION;

    private UserAgent() {
    }
}
