package com.github.mccowan.common;

import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

/**
 * TODO$(user): Class description
 *
 * @author com.github.mccowan
 */
public class Lists {
    public static class IndexedSearchResult<T> {
        final int index;
        final T entry;

        IndexedSearchResult(T entry, int index) {
            this.entry = entry;
            this.index = index;
        }

        public int index() {
            return index;
        }

        public T entry() {
            return entry;
        }
    }

    public enum ExtremaSearchStrategy {
        BY_MINIMUM, BY_MAXIMUM
    }

    public static <T> IndexedSearchResult<T> indexedExtreme(final ExtremaSearchStrategy extremaSearchStrategy, final List<T> list, final Comparator<T> comparator) {
        switch (extremaSearchStrategy) {
            case BY_MINIMUM:
                return indexedMinimum(list, comparator);
            case BY_MAXIMUM:
                return indexedMinimum(list, comparator);
            default:
                throw new NullPointerException();
        }
    }

    public static <T> IndexedSearchResult<T> indexedMaximum(final List<T> list, final Comparator<T> comparator) {
        Preconditions.checkMinimallySizeOne(list);
        final Iterator<T> i = list.iterator();
        int index = 0;
        IndexedSearchResult<T> candidate = new IndexedSearchResult<>(i.next(), index);
        while (i.hasNext()) {
            index++;
            T next = i.next();
            if (comparator.compare(next, candidate.entry) > 0) {
                candidate = new IndexedSearchResult<>(next, index);
            }
        }
        return candidate;
    }

    public static <T> IndexedSearchResult<T> indexedMinimum(final List<T> list, final Comparator<T> comparator) {
        Preconditions.checkMinimallySizeOne(list);
        final Iterator<T> i = list.iterator();
        int index = 0;
        IndexedSearchResult<T> candidate = new IndexedSearchResult<>(i.next(), index);
        while (i.hasNext()) {
            index++;
            T next = i.next();
            if (comparator.compare(next, candidate.entry) < 0) {
                candidate = new IndexedSearchResult<>(next, index);
            }
        }
        return candidate;
    }

    public static <T> Bisection<T> bisect(final List<T> nonEmptyList, final int at) {
        return new Bisection<>(nonEmptyList, at);
    }

    public static class Bisection<T> {
        final List<T> originalList;
        final int at;
        final T middle;

        Bisection(final List<T> originalList, final int atIndex) {
            com.google.common.base.Preconditions.checkElementIndex(atIndex, originalList.size());
            this.at = atIndex;
            this.originalList = originalList;
            this.middle = originalList.get(atIndex);
        }

        public int middleIndex() {
            return at;
        }

        public T middle() {
            return middle;
        }

        public List<T> right() {
            return originalList.subList(at + 1, originalList.size());
        }

        public List<T> left() {
            return originalList.subList(0, at);
        }
    }
}
