package com.github.mccowan.tap;

import com.github.mccowan.common.Log;
import com.github.mccowan.timeseries.SynchronousTimeSeries;
import com.github.mccowan.timeseries.TimeSeries;
import com.github.mccowan.timeseries.TimeSeriesObservation;

import java.util.Date;

/**
 * Responsible for detecting taps from streams of accelerometer data.
 * TODO: COnsider multithreading, or forcing message-passing for handler
 */
public class StreamingTapDetector {
    private final static String LOG_TAG = StreamingTapDetector.class.getCanonicalName();

    private final TapHandler clientHandler;
    private final TapDetector detector = TapDetector.withDefaults();
    private final TimeSeries<Acceleration> series = new SynchronousTimeSeries<>();// TODO: Would be nice to hide implementation via generator unless greater granularity is exposed here
    private long lastObservationNanos = 0;

    public StreamingTapDetector(final TapHandler clientHandler) {
        this.clientHandler = clientHandler;
    }

    /**
     * Incorporates the provided accuracy change into the tap-detection analysis.
     */
    public void acceptAccelerometerAccuracyChange(final Date observationTime, final int accuracy) {
        Log.d(LOG_TAG, String.format("ACCURACY\t%s ... umm, this method doesn't ... do any...thing ...", accuracy));
    }

    /**
     * Incorporates the provided sensor data into the tap-detection analysis.
     */
    public synchronized void acceptAccelerometerData(final long observationNanosecond, final double x, final double y, final double z) {
        series.add(new Acceleration(x, y, z, observationNanosecond));
        lastObservationNanos = observationNanosecond;
        searchAndRemoveTriggeredTaps();
        prune();
    }

    /**
     * Purges records from collected data that is deemed irrelevant.
     */
    private void prune() {
        series.pruneBefore(lastObservationNanos - detector.getTapWidthNanos());
    }

    /**
     * Performs analysis to determine if any taps were observed, and propagates call to clientHandler.
     */
    private void searchAndRemoveTriggeredTaps() {
        long latestObservation = 0;
        for (TimeSeriesObservation<Acceleration> tap : detector.detect(series)) {
            clientHandler.onTap(tap.point().getTime());
            latestObservation = tap.context().range().end;
        }
        series.pruneBefore(latestObservation);
    }

}
