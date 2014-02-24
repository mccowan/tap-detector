package com.github.mccowan.tap;

import com.github.mccowan.common.Lists;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

/**
 * TODO$(user): Class description
 *
 * @author com.github.mccowan
 */
public class ListBasedTapDetector {
    final long tapWidthNanos;
    final double accelerationPerturbationMagnitudeMinimum;

    public ListBasedTapDetector(final long tapWidthNanos, double accelerationPerterbationMagnitudeMinimum) {
        this.tapWidthNanos = tapWidthNanos;
        this.accelerationPerturbationMagnitudeMinimum = accelerationPerterbationMagnitudeMinimum;
    }

    public long getWidth() {
        return tapWidthNanos;
    }

    /**
     * Find the maximal element, then find the minimums after dividing the interval on that maximum.  It's a tap
     * if ... I'm not sure.
     * TODO: Make this less stupid.
     */
    @Nullable
    private Lists.IndexInterval searchTapInterval(final List<Acceleration> observations) {
        final Extrema.Result search = Extrema.search(observations);

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
        final Lists.Bisection<Acceleration> bisection = Lists.bisect(observations, extreme.index());

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
            return new Lists.IndexInterval(leftExtreme.index(), bisection.middleIndex() + 1 + rightExtreme.index());
        } else {
            return null;
        }
    }

    @Nullable
    public List<Lists.IndexInterval> detect(final List<Acceleration> observations) {
        final List<Lists.IndexInterval> tapSpans = new ArrayList<>();
        final AccelerationFrameIterator i = new AccelerationFrameIterator(getWidth(), observations);
        while (i.hasNext()) {
            final AccelerationFrameIterator.Frame nextFrame = i.next();
            final Lists.IndexInterval tapInterval = searchTapInterval(nextFrame.frameContents);
            if (tapInterval != null) {
                tapSpans.add(new Lists.IndexInterval(nextFrame.parentListOffset + tapInterval.start(), nextFrame.parentListOffset + tapInterval.end()));
                i.advance(tapInterval.end());
            }
        }
        return tapSpans;
    }
}
