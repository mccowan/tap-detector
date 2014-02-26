package com.github.mccowan.tap;

import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * This class doesn't actually perform any assertions; it's just here for human observation!
 *
 * @author mccowan
 */
public class StreamingTapDetectorTest {
    @Test
    public void testStreamingDetect() throws Exception {
        final List<Long> tapObservationTimes = Collections.synchronizedList(new ArrayList<Long>());
        
        final StreamingTapDetector detector = new StreamingTapDetector(new TapHandler() {
            @Override
            public void onTap(long nanoTime) {
                tapObservationTimes.add(nanoTime);
            }
        });

        final long moreThanZeroTimeOffset = System.nanoTime();
        for (Acceleration o : Data.ACCELEROMETER_OBSERVATIONS) {
            final long relativeNanoTime = System.nanoTime() - moreThanZeroTimeOffset;
            final long sleepMillis = TimeUnit.NANOSECONDS.toMillis(o.getNanoTime() - relativeNanoTime);
            if (sleepMillis > 0) {
                Thread.sleep(sleepMillis); // Why am I bothering to sleep in a single-threaded test?  Good question.
            }
            detector.acceptAccelerometerData(o.getTime(), o.getX(), o.getY(), o.getZ());
        }

        Assert.assertEquals(tapObservationTimes, Data.TAP_TIMES);
    }
}
