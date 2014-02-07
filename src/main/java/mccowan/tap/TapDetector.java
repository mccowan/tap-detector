package mccowan.tap;

import mccowan.util.Log;

import java.io.IOException;
import java.util.*;

/**
 * Responsible for detecting taps from streams of accelerometer data by accumulating data.
 * <p/>
 * TODO: For now, just collects and dumps data at request.
 */
public class TapDetector {
    private final static String LOG_TAG = TapDetector.class.getCanonicalName();
    public interface TapHandler {
        void onTap();
    }

    private final TapHandler tapHandler;
    final List<AccelerationVectorObservation> observations = Collections.synchronizedList(new ArrayList<AccelerationVectorObservation>(10000));

    public TapDetector(final TapHandler tapHandler) {
        this.tapHandler = tapHandler;
    }

    /** Incorporates the provided accuracy change into the tap-detection analysis. */
    public void acceptAccelerometerAccuracyChange(final Date observationTime, final int accuracy) {
        Log.d(LOG_TAG, String.format("ACCURACY\t%s", accuracy));
    }

    /** Incorporates the provided sensor data into the tap-detection analysis. */
    public synchronized void acceptAccelerometerData(final long observationNanosecond, final double x, final double y, final double z) {
        observations.add(new AccelerationVectorObservation(x, y, z, observationNanosecond));
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
        final Iterator<AccelerationVectorObservation> iterator = observations.iterator();
        Long start = null;
        int i = 0;
        while (iterator.hasNext()) {
            final AccelerationVectorObservation n = iterator.next();
            if (start == null) start = n.getNanoTime();
            Log.d("DATA", String.format("%s\t%s\t%s\t%s\t%s\t%s", ++i, n.getNanoTime() - start, n.getX(), n.getY(), n.getZ(), n.getMagnitude()));
            iterator.remove();
        }
    }
}
