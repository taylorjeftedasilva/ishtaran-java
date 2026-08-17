package com.ishtaran.sdk.pagination;

import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.function.BiFunction;

/**
 * Iterador lazy sobre um endpoint com paginação real {@code skip}/{@code take} — busca a próxima
 * página sob demanda, nunca carrega a coleção inteira de uma vez (regra do brief: "nunca bulk-
 * loading unbounded"). Usado só nos 2 endpoints do SDK com paginação real de verdade
 * (Withdrawals.list, Ledger.listEntries — ver SDK_CAPABILITY_SPEC.md §12.7); todo outro endpoint de
 * listagem não tem paginação real no servidor, então devolve {@code List<T>} simples (que já é
 * {@code Iterable}, cobrindo o caso de uso sem fingir paginação que a API não tem).
 */
public final class PageIterator<T> implements Iterable<T> {

    private final BiFunction<Integer, Integer, List<T>> fetchPage;
    private final int pageSize;

    public PageIterator(int pageSize, BiFunction<Integer, Integer, List<T>> fetchPage) {
        if (pageSize <= 0) {
            throw new IllegalArgumentException("pageSize deve ser positivo");
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
