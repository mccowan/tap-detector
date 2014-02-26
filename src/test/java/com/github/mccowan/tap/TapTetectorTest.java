package com.github.mccowan.tap;

import com.github.mccowan.timeseries.SynchronousTimeSeries;
import com.github.mccowan.timeseries.TimeSeriesObservation;
import org.testng.Reporter;
import org.testng.annotations.Test;

import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * TODO$(user): Class description
 *
 * @author mccowan
 */
public class TapTetectorTest {
    final static TapDetector DETECTOR = new TapDetector(500000000L, 0.5);

    @Test
    public void testDetect() throws Exception {
        final SynchronousTimeSeries<Acceleration> series = new SynchronousTimeSeries<>();
        for (Acceleration acceleration : Data.ACCELEROMETER_OBSERVATIONS) {
            series.add(acceleration);
        }
        final List<TimeSeriesObservation<Acceleration>> detect = DETECTOR.detect(series);
        long count = 0;
        for (TimeSeriesObservation<Acceleration> taps : detect) {
            Reporter.log(String.format("Tap #%d\t%f\t%s", ++count, taps.point().getTime() / (double) TimeUnit.NANOSECONDS.convert(1, TimeUnit.SECONDS), taps.point().getMagnitude()), true);
        }
    }

}
