package mccowan.tap;

import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;

/**
 * Walks through the list of {@link AccelerationVectorObservation}s, returning frames
 * (sublists) of the provided list.  The frames are returned in the order they appear in the provided list, which is assumed to be in
 * ascending order by {@link AccelerationVectorObservation#getNanoTime()}.  The frames returned will include as many elements as possible
 * without exceeding {@link #frameWidthNanos};
 */
public class AccelecerationVectorObservationFrameIterator implements Iterator<List<AccelerationVectorObservation>> {
    private final long frameWidthNanos;
    private final List<AccelerationVectorObservation> observations;

    /** Pointers to the indexes of the start and end elements of the current frame. */
    private int start = -1, end = -1; // Prime start at -1 to conform to logic for next calls.  End = -1 so hasNext returns false on empty.

    public AccelecerationVectorObservationFrameIterator(final long frameWidthNanos, final List<AccelerationVectorObservation> observations) {
        this.frameWidthNanos = frameWidthNanos;
        this.observations = observations;
    }

    @Override
    public boolean hasNext() {
        return end != observations.size() - 1;
    }

    @Override
    public List<AccelerationVectorObservation> next() {
        if (!hasNext()) throw new NoSuchElementException();

        if (start == -1) {
            // First invocation.  The start of our frame is always 0, so walk end candidates until the width of the frame is appropriate.
            start = end = 0;
            final long frameStartTime = observations.get(start).getNanoTime(); // First will necessarily be defined.
            while (observations.get(end).getNanoTime() - frameStartTime < frameWidthNanos) {
                end++;
            }
        } else {
            // Advance the end frame so that we include new data, and then find the appropriate start frame.
            end++;
            final AccelerationVectorObservation frameEnd = observations.get(end);
            while (observations.get(start).getNanoTime() - frameEnd.getNanoTime() < frameWidthNanos) {
                start++;
            }

            // Now advance end as far as possible.
            final long frameStartTime = observations.get(start).getNanoTime();
            while (observations.get(end).getNanoTime() - frameStartTime < frameWidthNanos) {
                end++;
            }
        }
        return observations.subList(start, end + 1);
    }

    @Override
    public void remove() {
        throw new UnsupportedOperationException();
    }
}
