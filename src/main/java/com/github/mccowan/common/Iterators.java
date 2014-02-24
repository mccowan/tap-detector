package com.github.mccowan.common;

import java.util.Iterator;

/**
 * TODO$(user): Class description
 *
 * @author mccowan
 */
public class Iterators {
    public interface UnmodifiableIterator<ELEMENT_TYPE, ITERATOR_TYPE> extends Iterator<ELEMENT_TYPE> {
    }

    /**
     * Produces an iterator from the provided {@link java.lang.Iterable} with the {@link java.util.Iterator#remove()}
     * method disabled.
     */
    public static <ELEMENT_TYPE, UNDERLYING_ITERABLE extends Iterable<ELEMENT_TYPE>> UnmodifiableIterator<ELEMENT_TYPE, UNDERLYING_ITERABLE>
    unmodifiableFor(final UNDERLYING_ITERABLE iterable) {
        final Iterator<ELEMENT_TYPE> underlyingIterator = iterable.iterator();
        return new UnmodifiableIterator<ELEMENT_TYPE, UNDERLYING_ITERABLE>() {
            @Override
            public boolean hasNext() {
                return underlyingIterator.hasNext();
            }

            @Override
            public ELEMENT_TYPE next() {
                return underlyingIterator.next();
            }

            @Override
            public void remove() {
                throw new UnsupportedOperationException();
            }
        };
    }
}
