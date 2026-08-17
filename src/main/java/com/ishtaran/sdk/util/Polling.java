package com.ishtaran.sdk.util;

import com.ishtaran.sdk.error.TimeoutError;

import java.time.Duration;
import java.time.Instant;
import java.util.function.Predicate;
import java.util.function.Supplier;

/**
 * Base compartilhada de todo {@code waitFor} do SDK (Easy Mode {@code payments.waitFor} e Core
 * {@code withdrawals().waitFor}/{@code transactions().waitFor}) — nunca polling infinito, sempre
 * {@code timeout} explícito (ver SDK_CAPABILITY_SPEC.md §15).
 */
public final class Polling {

    private Polling() {
    }

    public static <T> T until(Supplier<T> fetch, Predicate<T> isDone, Duration timeout, Duration pollInterval, String description) {
        var deadline = Instant.now().plus(timeout);
        while (true) {
            var result = fetch.get();
            if (isDone.test(result)) {
                return result;
            }
            if (Instant.now().isAfter(deadline)) {
                throw new TimeoutError("waitFor excedeu o timeout de " + timeout + " aguardando " + description, null);
            }
            sleep(pollInterval);
        }
    }

    private static void sleep(Duration duration) {
        try {
            Thread.sleep(duration.toMillis());
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new TimeoutError("waitFor interrompido", e);
        }
    }
}
