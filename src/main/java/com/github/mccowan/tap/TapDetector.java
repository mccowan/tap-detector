package com.github.mccowan.tap;

import com.github.mccowan.common.Lists;
import com.github.mccowan.timeseries.SynchronousTimeSeries;
import com.github.mccowan.timeseries.TimeSeries;
import com.github.mccowan.timeseries.TimeSeriesView;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterators;
import com.google.common.collect.Range;
import com.google.common.collect.Ranges;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

/**
 * TODO$(user): Class description
 *
 * @author mccowan
 */
public class TapDetector {
    final long tapWidthNanos;
    final double accelerationPerturbationMagnitudeMinimum;

    public TapDetector(final long tapWidthNanos, double accelerationPerterbationMagnitudeMinimum) {
        this.tapWidthNanos = tapWidthNanos;
        this.accelerationPerturbationMagnitudeMinimum = accelerationPerterbationMagnitudeMinimum;
    }

    /**
     * Find the maximal element, then find the minimums after dividing the interval on that maximum.  It's a tap
     * if ... I'm not sure.
     * TODO: Make this less stupid.
     */
    @Nullable
    private SynchronousTimeSeries.TimeRange searchTapInterval(final Iterable<Acceleration> observations) {
        final ImmutableList<Acceleration> observationList = ImmutableList.copyOf(observations);
        final Extrema.Result search = Extrema.search(observationList);

        final double maxDifference = Math.abs(search.averageMagnitude - search.maximum.entry().getMagnitude());
        final double minDifference = Math.abs(search.averageMagnitude - search.minimum.entry().getMagnitude());

        final Lists.IndexedSearchResult<Acceleration> extreme;
        final Lists.ExtremaSearchStrategy nonTapExtremaSearchStrategy;
        if (maxDifference > minDifference) {
            extreme = search.maximum;
            nonTapExtremaSearchStrategy = Lists.ExtremaSearchStrategy.BY_MINIMUM;
        } else {
            extreme = search.minimum;
            nonTapExtremaSearchStrategy = Lists.ExtremaSearchStrategy.BY_MAXIMUM;
        }
        final Lists.Bisection<Acceleration> bisection = Lists.bisect(observationList, extreme.index());

        if (bisection.left().isEmpty() || bisection.right().isEmpty()) {
            // TODO: Is this really the right approach?
            return null;
        }

        final Lists.IndexedSearchResult<Acceleration> leftExtreme =
                Lists.indexedExtreme(nonTapExtremaSearchStrategy, bisection.left(), Acceleration.MAGNITUDE_COMPARATOR);
        final Lists.IndexedSearchResult<Acceleration> rightExtreme =
                Lists.indexedExtreme(nonTapExtremaSearchStrategy, bisection.right(), Acceleration.MAGNITUDE_COMPARATOR);

        if (Math.abs(extreme.entry().getMagnitude() - leftExtreme.entry().getMagnitude()) > accelerationPerturbationMagnitudeMinimum
                && Math.abs(extreme.entry().getMagnitude() - rightExtreme.entry().getMagnitude()) > accelerationPerturbationMagnitudeMinimum) {
            return new SynchronousTimeSeries.TimeRange(leftExtreme.entry().getTime(), rightExtreme.entry().getTime());
        } else {
            return null;
        }
    }

    // TODO: Terribly nonperformant, just to get it working with old code
    // TODO: skip intervals that are already considered for tapping
    public List<TimeSeriesView<Acceleration>> detect(final TimeSeries<Acceleration> observations) {
        final List<TimeSeriesView<Acceleration>> tapContainingSeries = new ArrayList<>();
        long lastObservedTapIntervalEnd = Long.MIN_VALUE;
        for (final TimeSeriesView<Acceleration> accelerations : observations.frameIterator(tapWidthNanos)) {
            if (accelerations.range().start >= lastObservedTapIntervalEnd) {
                final SynchronousTimeSeries.TimeRange tapInterval = searchTapInterval(accelerations);
                if (tapInterval != null) {
                    tapContainingSeries.add(observations.viewBy(tapInterval.start, tapInterval.end));
                    lastObservedTapIntervalEnd = tapInterval.end;
                }
            }
        }
        return tapContainingSeries;
    }
}