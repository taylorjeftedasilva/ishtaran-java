package com.ishtaran.sdk.util;

/** Fixed format {@code ishtaran-java/<version>} -- never contains personal data (rule from the brief). */
public final class UserAgent {

    public static final String SDK_VERSION = "0.1.3";
    public static final String DEFAULT = "ishtaran-java/" + SDK_VERSION;

    private UserAgent() {
    }
}
