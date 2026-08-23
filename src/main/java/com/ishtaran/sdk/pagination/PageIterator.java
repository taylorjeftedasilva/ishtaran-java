package com.ishtaran.sdk.pagination;

import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.function.BiFunction;

/**
 * Lazy iterator over an endpoint with real {@code skip}/{@code take} pagination — fetches the next
 * page on demand, never loads the whole collection at once (rule from the brief: "never unbounded
 * bulk-loading"). Used only on the 2 SDK endpoints with genuinely real pagination
 * (Withdrawals.list, Ledger.listEntries — see SDK_CAPABILITY_SPEC.md §12.7); every other listing
 * endpoint has no real server-side pagination, so it returns a plain {@code List<T>} (which is
 * already {@code Iterable}, covering the use case without faking pagination the API doesn't have).
 */
public final class PageIterator<T> implements Iterable<T> {

    private final BiFunction<Integer, Integer, List<T>> fetchPage;
    private final int pageSize;

    public PageIterator(int pageSize, BiFunction<Integer, Integer, List<T>> fetchPage) {
        if (pageSize <= 0) {
            throw new IllegalArgumentException("pageSize must be positive");
        }
        this.pageSize = pageSize;
        this.fetchPage = fetchPage;
    }

    @Override
    public Iterator<T> iterator() {
        return new Iterator<>() {
            private int skip = 0;
            private List<T> currentPage = null;
            private int indexInPage = 0;
            private boolean exhausted = false;

            @Override
            public boolean hasNext() {
                ensurePageLoaded();
                return currentPage != null && indexInPage < currentPage.size();
            }

            @Override
            public T next() {
                if (!hasNext()) {
                    throw new NoSuchElementException();
                }
                return currentPage.get(indexInPage++);
            }

            private void ensurePageLoaded() {
                if (exhausted) {
                    return;
                }
                if (currentPage == null || indexInPage >= currentPage.size()) {
                    if (currentPage != null && currentPage.size() < pageSize) {
                        exhausted = true;
                        return;
                    }
                    currentPage = fetchPage.apply(skip, pageSize);
                    indexInPage = 0;
                    skip += pageSize;
                    if (currentPage.isEmpty()) {
                        exhausted = true;
                    }
                }
            }
        };
    }
}
