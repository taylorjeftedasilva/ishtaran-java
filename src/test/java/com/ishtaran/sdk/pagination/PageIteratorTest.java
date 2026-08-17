package com.ishtaran.sdk.pagination;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

import static org.junit.jupiter.api.Assertions.assertEquals;

/** Ver SDK_CAPABILITY_SPEC.md §12.7 — nunca carrega tudo de uma vez, busca página sob demanda. */
class PageIteratorTest {

    @Test
    void iteratesAcrossMultiplePages_neverFetchingAllAtOnce() {
        List<Integer> allItems = IntStream.range(0, 25).boxed().toList();
        var fetchCalls = new ArrayList<Integer>();

        var pageIterator = new PageIterator<Integer>(10, (skip, take) -> {
            fetchCalls.add(skip);
            return allItems.stream().skip(skip).limit(take).toList();
        });

        var collected = new ArrayList<Integer>();
        for (var item : pageIterator) {
            collected.add(item);
        }

        assertEquals(allItems, collected);
        assertEquals(List.of(0, 10, 20), fetchCalls); // 3 páginas: 10+10+5, nunca busca a 4a
    }

    @Test
    void emptyResult_neverFetchesMoreThanOncePage() {
        var fetchCalls = new ArrayList<Integer>();
        var pageIterator = new PageIterator<Integer>(10, (skip, take) -> {
            fetchCalls.add(skip);
            return List.of();
        });

        var collected = new ArrayList<Integer>();
        for (var item : pageIterator) {
            collected.add(item);
        }

        assertEquals(List.of(), collected);
        assertEquals(1, fetchCalls.size());
    }

    @Test
    void exactPageSizeBoundary_fetchesOneExtraEmptyPage_thenStops() {
        // 10 items, pageSize 10: a primeira página vem cheia (size==pageSize), então o iterador
        // não sabe ainda que acabou -- busca a próxima e recebe vazia.
        List<Integer> allItems = IntStream.range(0, 10).boxed().toList();
        var fetchCalls = new ArrayList<Integer>();
        var pageIterator = new PageIterator<Integer>(10, (skip, take) -> {
            fetchCalls.add(skip);
            return allItems.stream().skip(skip).limit(take).toList();
        });

        var collected = new ArrayList<Integer>();
        for (var item : pageIterator) {
            collected.add(item);
        }

        assertEquals(allItems, collected);
        assertEquals(List.of(0, 10), fetchCalls);
    }
}
