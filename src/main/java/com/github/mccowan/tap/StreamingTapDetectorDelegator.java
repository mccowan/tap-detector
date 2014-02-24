package com.github.mccowan.tap;

import com.github.mccowan.common.Log;

import java.io.IOException;
import java.util.*;

/**
 * Responsible for detecting taps from streams of accelerometer data by accumulating data.
 * <p/>
 * TODO: For now, just collects and dumps data at request.
 */
public class StreamingTapDetectorDelegator {
    private final static String LOG_TAG = StreamingTapDetectorDelegator.class.getCanonicalName();
    public interface TapHandler {
        void onTap();
    }

    private final TapHandler tapHandler;
    private final ListBasedTapDetector tapDetector;
    final List<Acceleration> observations = Collections.synchronizedList(new ArrayList<Acceleration>(10000));

    public StreamingTapDetectorDelegator(final TapHandler tapHandler, ListBasedTapDetector tapDetector, long expectedTapDurationNanos) {
        this.tapHandler = tapHandler;
        this.tapDetector = tapDetector;
    }

    /** Incorporates the provided accuracy change into the tap-detection analysis. */
    public void acceptAccelerometerAccuracyChange(final Date observationTime, final int accuracy) {
        Log.d(LOG_TAG, String.format("ACCURACY\t%s", accuracy));
    }

    /** Incorporates the provided sensor data into the tap-detection analysis. */
    public synchronized void acceptAccelerometerData(final long observationNanosecond, final double x, final double y, final double z) {
        observations.add(new Acceleration(x, y, z, observationNanosecond));
        searchAndRemoveTriggeredTaps();
        prune();
    }

    private void prune() {
        Log.d(LOG_TAG, "Uh, not pruning?");
    }

    private void searchAndRemoveTriggeredTaps() {
        Log.d(LOG_TAG, "Uh, not pruning?");
    }

    public synchronized void writeAndPurgeData() throws IOException {
        final Iterator<Acceleration> iterator = observations.iterator();
        Long start = null;
        int i = 0;
        while (iterator.hasNext()) {
            final Acceleration n = iterator.next();
            if (start == null) start = n.getNanoTime();
            Log.d("DATA", String.format("%s\t%s\t%s\t%s\t%s\t%s", ++i, n.getNanoTime() - start, n.getX(), n.getY(), n.getZ(), n.getMagnitude()));
            iterator.remove();
        }
    }
}
