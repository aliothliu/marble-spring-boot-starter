package io.github.aliothliu.marble.rbac.domain;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class SortTest {

    @Test
    void initialSort() {
        assertEquals(new Sort(1), Sort.one());
    }

    @Test
    void increment() {
        Sort sort = Sort.one();
        sort.increment();

        assertEquals(new Sort(2), sort);
    }

    @Test
    void decrement() {
        Sort sort = Sort.one();
        sort.decrement();

        assertEquals(new Sort(0), sort);
    }

    @Test
    void compareSort() {
        assertEquals(-1, Sort.one().compareTo(new Sort(5)));
        assertEquals(0, Sort.one().compareTo(new Sort(1)));
        assertEquals(1, new Sort(10).compareTo(new Sort(5)));
    }
}