package com.ishtaran.sdk.pagination;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

import static org.junit.jupiter.api.Assertions.assertEquals;

/** See SDK_CAPABILITY_SPEC.md §12.7 — never loads everything at once, fetches pages on demand. */
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
        assertEquals(List.of(0, 10, 20), fetchCalls); // 3 pages: 10+10+5, never fetches the 4th
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
        // 10 items, pageSize 10: the first page comes back full (size==pageSize), so the iterator
        // doesn't yet know it's finished -- it fetches the next one and gets an empty page back.
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
