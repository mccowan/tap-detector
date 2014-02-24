package com.github.mccowan.timeseries;

import java.util.Iterator;

/**
 * TODO$(user): Class description
 *
 * @author mccowan
 */
public interface TimeSeriesView<T extends TimedEntity> extends Iterable<T> {
    /**
     * Returns an iterator over all unique frames in this {@link com.github.mccowan.timeseries.TimeSeriesView} of width
     * at most {@code frameWidth}, except for any frame whose elements are a subset of another.
     */
    Iterable<TimeSeriesView<T>> frameIterator(long frameWidth);

    TimeSeriesView<T> viewBy(long start, long end);


    /** The range this time series spans. */
    SynchronousTimeSeries.TimeRange range();
    
    int size();
}
