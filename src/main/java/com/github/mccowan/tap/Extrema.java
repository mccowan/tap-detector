package com.github.mccowan.tap;

import com.github.mccowan.common.Lists;

import java.util.List;

/**
 * TODO$(user): Class description
 *
 * @author com.github.mccowan
 */
public class Extrema {
    public static class Result {
        final double averageMagnitude;
        final Lists.IndexedSearchResult<Acceleration> maximum, minimum;

        Result(double averageMagnitude, Lists.IndexedSearchResult<Acceleration> maximum, Lists.IndexedSearchResult<Acceleration> minimum) {
            this.averageMagnitude = averageMagnitude;
            this.maximum = maximum;
            this.minimum = minimum;
        }
    }

    public static Result search(final List<Acceleration> accelerations) {
        double total = 0d;
        for (Acceleration acceleration : accelerations) {
            total += acceleration.getMagnitude();
        }

        return new Result(
                total / accelerations.size(),
                Lists.indexedMaximum(accelerations, Acceleration.MAGNITUDE_COMPARATOR),
                Lists.indexedMinimum(accelerations, Acceleration.MAGNITUDE_COMPARATOR)
        );
    }
}
