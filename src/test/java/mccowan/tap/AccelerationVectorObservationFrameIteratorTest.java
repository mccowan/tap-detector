package mccowan.tap;

import com.google.common.collect.ImmutableList;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class AccelerationVectorObservationFrameIteratorTest {

    private static final class EqualsingAccelerationVectorObservation extends AccelerationVectorObservation {
        EqualsingAccelerationVectorObservation(final double x, final double y, final double z, final long observationNanoTime) {
            super(x, y, z, observationNanoTime);
        }

        @Override
        public boolean equals(final Object o) {
            return getNanoTime() == ((EqualsingAccelerationVectorObservation) o).getNanoTime();
        }
    }

    static AccelerationVectorObservation g(final long nanoTime) {
        return new EqualsingAccelerationVectorObservation(0, 0, 0, nanoTime);
    }

    final static List<AccelerationVectorObservation> OBSERVATIONS = ImmutableList.of(
            g(0),
            g(10),
            g(25),
            g(50),
            g(250),
            g(251),
            g(251),
            g(258),
            g(300),
            g(301)
    );

    final static List<? extends List<AccelerationVectorObservation>> FIFTY_NS_EXPECTED_FRAMES = ImmutableList.of(
            ImmutableList.of(g(0), g(10), g(25), g(50)),
            ImmutableList.of(g(250), g(251), g(251), g(258), g(300)),
            ImmutableList.of(g(251), g(251), g(258), g(300), g(301))
    );

    @Test
    public void simpleTest() {
        final AccelecerationVectorObservationFrameIterator i = new AccelecerationVectorObservationFrameIterator(50, OBSERVATIONS);
        final List<List<AccelerationVectorObservation>> observed = new ArrayList<List<AccelerationVectorObservation>>();
        while (i.hasNext()) {
            observed.add(i.next());
        }
        Assert.assertEquals(observed, FIFTY_NS_EXPECTED_FRAMES);
    }
}
