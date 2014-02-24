package com.github.mccowan.timeseries;

import com.github.mccowan.common.Iterators;
import com.google.common.base.Function;
import com.google.common.collect.FluentIterable;
import com.google.common.collect.PeekingIterator;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.PriorityQueue;

/**
 * Non-thread-safe implementation of {@link com.github.mccowan.timeseries.TimeSeries}.
 * <p/>
 * TODO: Need to galvanize what's going on with all the simultaneous iterators here... it is a problemo.
 *
 * @author mccowan
 */
public class SynchronousTimeSeries<T extends TimedEntity> implements TimeSeries<T> {
    //TODO: Refacotr to use google Range
    public static class TimeRange {
        public final long start, end;

        @Override
        public String toString() {
            return String.format("%d-%d", start, end);
        }

        public TimeRange(long start, long end) {
            this.start = start;
            this.end = end;
        }
    }

    // TODO: Not the greatest implementation; almost all cases = add to end, this probably does binary serch each time
    final PriorityQueue<T> orderedElements;

    public SynchronousTimeSeries() {
        orderedElements = new PriorityQueue<>(11, TimedEntities.ASCENDING_COMPARATOR);
    }

    @Override
    public void pruneBefore(long youngest) {
        final Iterator<T> i = orderedElements.iterator();
        while (i.hasNext()) {
            if (i.next().getTime() < youngest)
                i.remove();
            else
                break;
        }
    }

    @Override
    public void add(T t) {
        orderedElements.add(t);
    }

    @Override
    public TimeRange range() {
        if (orderedElements.isEmpty()) {
            return new TimeRange(0, 0); // TODO
        } else {
            // TODO: SO INEFFICIENT
            final Iterator<T> i = orderedElements.iterator();
            final T start = i.next();
            T last = start;
            while (i.hasNext()) last = i.next();
            return new TimeRange(start.getTime(), last.getTime());
        }
    }


    @Override
    public TimeSeriesView<T> viewBy(long start, long end) {
        // TODO: if this implementation were indexable, this might be less horrible
        final SynchronousTimeSeries<T> ts = new SynchronousTimeSeries<>();
        for (T t : this) {
            if (t.getTime() >= start && t.getTime() <= end) {
                ts.add(t);
            }
            if (t.getTime() > end)
                break;
        }
        return ts;
    }

    @Override
    public int size() {
        return orderedElements.size();
    }

    @Override
    public Iterator<T> iterator() {
        return Iterators.unmodifiableFor(orderedElements);
    }


    @Override
    public Iterable<TimeSeriesView<T>> frameIterator(final long frameWidth) {
        final PeekingIterator<T> startIterator = com.google.common.collect.Iterators.peekingIterator(this.iterator());
        final PeekingIterator<T> endIterator = com.google.common.collect.Iterators.peekingIterator(this.iterator());
        final ArrayList<TimeRange> ranges = new ArrayList<>();
        while (endIterator.hasNext()) {
            // Advance the end iterator, so we're guaranteed to incorporate new data into the frame.
            final T minimumEnd = endIterator.next();
            final long minimumEndTime = minimumEnd.getTime();
            // Advance the start iterator, so that the end iterator falls into the frame
            T start = startIterator.next();
            while (startIterator.hasNext() && (minimumEndTime - start.getTime() > frameWidth))
                start = startIterator.next();
            final long startTime = start.getTime();

            // The start position is now decided; advance the end iterator such that we incorporate all elements that 
            // fall into start position + frame width
            T end = minimumEnd;
            while (endIterator.hasNext() && endIterator.peek().getTime() - startTime <= frameWidth)
                end = endIterator.next();
            ranges.add(new TimeRange(startTime, end.getTime()));
        }

        return FluentIterable.from(ranges)
                .transform(new Function<TimeRange, TimeSeriesView<T>>() {
                    @Override
                    public TimeSeriesView<T> apply(final TimeRange input) {
                        return viewBy(input.start, input.end);
                    }
                });
    }
}
