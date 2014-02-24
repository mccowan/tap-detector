package com.github.mccowan.tap;

import com.github.mccowan.common.Preconditions;

import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;

/**
 * Walks through the list of {@link Acceleration}s, returning frames
 * (sublists) of the provided list.  The frames are returned in the order they appear in the provided list, which is assumed to be in
 * ascending order by {@link Acceleration#getNanoTime()}.  The frames returned will include as many elements as possible
 * without exceeding {@link #frameWidthNanos}.
 */
public class AccelerationFrameIterator implements Iterator<AccelerationFrameIterator.Frame> {
    public static class Frame {
        final int parentListOffset;
        final List<Acceleration> frameContents;

        Frame(int parentListOffset, List<Acceleration> frameContents) {
            this.parentListOffset = parentListOffset;
            this.frameContents = frameContents;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            Frame frame = (Frame) o;

            if (parentListOffset != frame.parentListOffset) return false;
            if (!frameContents.equals(frame.frameContents)) return false;

            return true;
        }

        @Override
        public int hashCode() {
            int result = parentListOffset;
            result = 31 * result + frameContents.hashCode();
            return result;
        }

        @Override
        public String toString() {
            return "Frame{" +
                    "parentListOffset=" + parentListOffset +
                    ", frameContents=" + frameContents +
                    '}';
        }
    }

    private final long frameWidthNanos;
    private final List<Acceleration> observations;

    /**
     * Pointers to the indexes of the start and end elements of the current frame.
     */
    private int start = -1, end = -1; // Prime start at -1 to conform to logic for next calls.  End = -1 so hasNext returns false on empty.

    public AccelerationFrameIterator(final long frameWidthNanos, final List<Acceleration> observations) {
        Preconditions.checkSorted(observations, Acceleration.OBSERVATION_TIME_COMPARATOR, Preconditions.SortDirection.ASCENDING);
        this.frameWidthNanos = frameWidthNanos;
        this.observations = observations;
    }

    @Override
    public boolean hasNext() {
        return end != observations.size() - 1;
    }

    @Override
    public Frame next() {
        if (!hasNext()) throw new NoSuchElementException();

        if (start == -1) {
            start = end = 0;
            advanceEndUntilJustBeforeFrameWithExceededOrObservationsExhausted(observations.get(start).getNanoTime());
        } else {
            // Advance the end frame once so that we include new data, and then trace the start forward to find the appropriate start frame the appropriate start frame.
            end++;
            final long frameEndTime = observations.get(end).getNanoTime();
            while (start < end && frameEndTime - observations.get(start).getNanoTime() > frameWidthNanos) {
                start++;
            }

            // Now advance end as far as possible.
            advanceEndUntilJustBeforeFrameWithExceededOrObservationsExhausted(observations.get(start).getNanoTime());
        }
        return new Frame(start, observations.subList(start, end + 1));
    }

    private void advanceEndUntilJustBeforeFrameWithExceededOrObservationsExhausted(final long startTimeNanos) {
        int candidateEnd = end + 1;

        while (candidateEnd < observations.size() && observations.get(candidateEnd).getNanoTime() - startTimeNanos <= frameWidthNanos) {
            candidateEnd++;
        }
        end = candidateEnd - 1;
    }

    @Override
    public void remove() {
        throw new UnsupportedOperationException();
    }

    /**
     * Advance the iterator to consider frames only beyond the provided end index.
     */
    public void advance(final int end) {
        com.google.common.base.Preconditions.checkElementIndex(end, observations.size());
        com.google.common.base.Preconditions.checkArgument(end >= this.start, "can't advance backwards");
        this.start = end;
    }
}
