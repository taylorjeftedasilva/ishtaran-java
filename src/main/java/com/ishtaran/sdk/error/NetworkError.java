package com.ishtaran.sdk.error;

/** Falha de transporte — sem nenhuma resposta HTTP (conexão recusada/reset, DNS, etc.). Sempre retryable. */
public final class NetworkError extends IshtaranError {
    public NetworkError(String message, Throwable cause) {
        super(message, null, null, null, null, true);
        if (cause != null) {
            initCause(cause);
        }
    }
}
