package com.github.mccowan.timeseries;

/**
 * Collator for a collection of {@link com.github.mccowan.timeseries.TimedEntity}s.
 *
 * @author mccowan
 */
public interface TimeSeries<T extends TimedEntity> extends TimeSeriesView<T> {
    /**
     * Purges all elements younger than the provided time.
     */
    void pruneBefore(long youngest);

    /**
     * Incorporates the provided {@link T} into the series.
     */
    void add(T t);

}
