package com.github.mccowan.tap;

import com.github.mccowan.common.Lists;
import com.github.mccowan.timeseries.TimeSeries;
import com.github.mccowan.timeseries.TimeSeriesObservation;
import com.github.mccowan.timeseries.TimeSeriesView;
import com.google.common.collect.ImmutableList;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

/**
 * Performs analyses of {@link com.github.mccowan.timeseries.TimeSeries} over
 * {@link com.github.mccowan.tap.Acceleration} data that represents force applied to an object subject to
 * tapping by human fingers.
 * 
 * todo: oh JESUS there are so many problems here
 * todo: - performance - oh god
 * todo: - detection mechanism - something more sophisticated? this is primitive but works (?) and is understasndable.
 * todo: - big failing: can't detect narrow taps
 *
 * @author mccowan
 */
public class TapDetector {
    final long tapWidthNanos;

    public long getTapWidthNanos() {
        return tapWidthNanos;
    }

    final double accelerationPerturbationMagnitudeMinimum;

    public static class Defaults {
        final static long TAP_WIDTH_NANOS = 500000000L;
        final static double ACCELERATION_PERTERBATION_MINIMUM_MAGNITUDE = 0.5;
    }

    static TapDetector withDefaults() {
        return new TapDetector(Defaults.TAP_WIDTH_NANOS, Defaults.ACCELERATION_PERTERBATION_MINIMUM_MAGNITUDE);
    }

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
    private TimeSeriesObservation<Acceleration> searchTapInterval(final TimeSeriesView<Acceleration> series) {
        final ImmutableList<Acceleration> observationList = ImmutableList.copyOf(series);
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
            final TimeSeriesView<Acceleration> context = series.viewBy(leftExtreme.entry().getTime(), rightExtreme.entry().getTime());
            return new TimeSeriesObservation<Acceleration>() {
                @Override
                public TimeSeriesView<Acceleration> context() {
                    return context;
                }

                @Override
                public Acceleration point() {
                    return extreme.entry();
                }
            };
        } else {
            return null;
        }
    }

    // TODO: Terribly nonperformant, just to get it working with old code
    // TODO: skip intervals that are already considered for tapping
    public List<TimeSeriesObservation<Acceleration>> detect(final TimeSeries<Acceleration> observations) {
        final List<TimeSeriesObservation<Acceleration>> tapContainingSeries = new ArrayList<>();
        long lastObservedTapIntervalEnd = Long.MIN_VALUE;
        for (final TimeSeriesView<Acceleration> accelerations : observations.frameIterator(tapWidthNanos)) {
            if (accelerations.range().start >= lastObservedTapIntervalEnd) {
                final TimeSeriesObservation<Acceleration> tap = searchTapInterval(accelerations);
                if (tap != null) {
                    tapContainingSeries.add(tap);
                    lastObservedTapIntervalEnd = tap.context().range().end;
                }
            }
        }
        return tapContainingSeries;
    }
}