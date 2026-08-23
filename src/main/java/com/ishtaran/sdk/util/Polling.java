package com.ishtaran.sdk.util;

import com.ishtaran.sdk.error.TimeoutError;

import java.time.Duration;
import java.time.Instant;
import java.util.function.Predicate;
import java.util.function.Supplier;

/**
 * Shared base for every {@code waitFor} in the SDK (Easy Mode {@code payments.waitFor} and Core
 * {@code withdrawals().waitFor}/{@code transactions().waitFor}) -- never infinite polling, always
 * an explicit {@code timeout} (see SDK_CAPABILITY_SPEC.md section 15).
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
                throw new TimeoutError("waitFor exceeded the timeout of " + timeout + " waiting for " + description, null);
            }
            sleep(pollInterval);
        }
    }

    private static void sleep(Duration duration) {
        try {
            Thread.sleep(duration.toMillis());
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new TimeoutError("waitFor interrupted", e);
        }
    }
}
