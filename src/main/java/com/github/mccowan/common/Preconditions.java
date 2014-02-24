package com.github.mccowan.common;

import com.google.common.collect.Ordering;

import java.util.Collection;
import java.util.Comparator;

/**
 * TODO$(user): Class description
 *
 * @author com.github.mccowan
 */
public class Preconditions {
    static <T> void checkMinimallySizeOne(final Collection<T> list) {
        com.google.common.base.Preconditions.checkElementIndex(0, list.size(), "require size one");
    }

    public enum SortDirection {
        ASCENDING, DESCENDING
    }
    
    public static <T> void checkSorted(final Iterable<T> sorted, final Comparator<T> comparator, final SortDirection order) {
        final Ordering<T> ordering = Ordering.from(comparator);
        switch (order) {
            case ASCENDING: break;
            case DESCENDING: ordering.reverse();
        }
        if (!ordering.isOrdered(sorted)) {
            throw new IllegalArgumentException();
        }
    }
}
