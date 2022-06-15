package io.github.aliothliu.marble.rbac.domain;

import lombok.Getter;

import java.io.Serializable;
import java.util.Objects;

/**
 * Sort
 *
 * @author liubin
 **/
@Getter
public class Sort implements Comparable<Sort>, Serializable {

    private int sort;

    protected Sort() {
        this.sort = -1;
    }

    public Sort(int sort) {
        this.sort = sort;
    }

    public static Sort one() {
        return new Sort(1);
    }

    public static Sort zero() {
        return new Sort(1);
    }

    public void increment() {
        this.sort = this.sort + 1;
    }

    public void decrement() {
        this.sort = this.sort - 1;
    }

    @Override
    public int compareTo(Sort other) {
        return Long.compare(sort, other.getSort());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Sort)) {
            return false;
        }
        Sort sort1 = (Sort) o;
        return sort == sort1.sort;
    }

    @Override
    public int hashCode() {
        return Objects.hash(sort);
    }
}
