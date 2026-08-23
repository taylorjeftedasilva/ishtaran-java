package com.ishtaran.sdk.config;

import com.ishtaran.sdk.util.Redactor;
import com.ishtaran.sdk.util.UserAgent;

import java.time.Duration;
import java.util.Objects;

/**
 * Single central client configuration — source of truth for defaults (baseUrl/apiKey/timeout/
 * environment/userAgent/retryPolicy), never scattered across individual resources. Immutable after
 * construction.
 */
public final class IshtaranClientConfig {

    private final String baseUrl;
    private final Environment environment;
    private final String apiKey;
    private final Duration connectTimeout;
    private final Duration requestTimeout;
    private final RetryPolicy retryPolicy;
    private final String userAgent;
    private final boolean loggingEnabled;
    private final boolean allowInsecureTlsForLocalDevelopment;

    private IshtaranClientConfig(Builder builder) {
        this.environment = Objects.requireNonNullElse(builder.environment, Environment.LOCAL);
        this.baseUrl = Endpoints.resolve(this.environment, builder.baseUrl);
        this.apiKey = builder.apiKey;
        this.connectTimeout = Objects.requireNonNullElse(builder.connectTimeout, Duration.ofSeconds(5));
        this.requestTimeout = Objects.requireNonNullElse(builder.requestTimeout, Duration.ofSeconds(30));
        this.retryPolicy = Objects.requireNonNullElse(builder.retryPolicy, RetryPolicy.defaults());
        this.userAgent = Objects.requireNonNullElse(builder.userAgent, UserAgent.DEFAULT);
        this.loggingEnabled = builder.loggingEnabled;
        this.allowInsecureTlsForLocalDevelopment = builder.allowInsecureTlsForLocalDevelopment;

        if (this.allowInsecureTlsForLocalDevelopment && this.environment != Environment.LOCAL) {
            throw new IllegalStateException(
                    "allowInsecureTlsForLocalDevelopment can only be used with Environment.LOCAL — "
                            + "never against Sandbox/Production.");
        }
    }

    public static Builder builder() {
        return new Builder();
    }

    public String baseUrl() {
        return baseUrl;
    }

    public Environment environment() {
        return environment;
    }

    public String apiKey() {
        return apiKey;
    }

    public Duration connectTimeout() {
        return connectTimeout;
    }

    public Duration requestTimeout() {
        return requestTimeout;
    }

    public RetryPolicy retryPolicy() {
        return retryPolicy;
    }

    public String userAgent() {
        return userAgent;
    }

    public boolean loggingEnabled() {
        return loggingEnabled;
    }

    public boolean allowInsecureTlsForLocalDevelopment() {
        return allowInsecureTlsForLocalDevelopment;
    }

    @Override
    public String toString() {
        return "IshtaranClientConfig{baseUrl=" + baseUrl + ", environment=" + environment
                + ", apiKey=" + Redactor.mask(apiKey) + ", connectTimeout=" + connectTimeout
                + ", requestTimeout=" + requestTimeout + ", userAgent=" + userAgent + "}";
    }

    public static final class Builder {
        private String baseUrl;
        private Environment environment;
        private String apiKey;
        private Duration connectTimeout;
        private Duration requestTimeout;
        private RetryPolicy retryPolicy;
        private String userAgent;
        private boolean loggingEnabled;
        private boolean allowInsecureTlsForLocalDevelopment;

        public Builder baseUrl(String baseUrl) {
            this.baseUrl = baseUrl;
            return this;
        }

        public Builder environment(Environment environment) {
            this.environment = environment;
            return this;
        }

        public Builder apiKey(String apiKey) {
            this.apiKey = apiKey;
            return this;
        }

        public Builder connectTimeout(Duration connectTimeout) {
            this.connectTimeout = connectTimeout;
            return this;
        }

        public Builder requestTimeout(Duration requestTimeout) {
            this.requestTimeout = requestTimeout;
            return this;
        }

        public Builder retryPolicy(RetryPolicy retryPolicy) {
            this.retryPolicy = retryPolicy;
            return this;
        }

        public Builder userAgent(String userAgent) {
            this.userAgent = userAgent;
            return this;
        }

        public Builder enableLogging(boolean enabled) {
            this.loggingEnabled = enabled;
            return this;
        }

        /** Never use against Sandbox/Production — throws in {@code build()} if combined with another Environment. */
        public Builder allowInsecureTlsForLocalDevelopment(boolean allow) {
            this.allowInsecureTlsForLocalDevelopment = allow;
            return this;
        }

        public IshtaranClientConfig build() {
            return new IshtaranClientConfig(this);
        }
    }
}
