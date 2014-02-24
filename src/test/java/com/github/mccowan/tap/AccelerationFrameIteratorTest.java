package com.github.mccowan.tap;

import com.google.common.base.Joiner;
import com.google.common.collect.ImmutableList;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.ArrayList;
import java.util.List;

public class AccelerationFrameIteratorTest {

    final static List<Acceleration> OBSERVATIONS = ImmutableList.of(
            t(0),
            t(10),
            t(25),
            t(50),
            t(250),
            t(251),
            t(251),
            t(258),
            t(300),
            t(301)
    );
    final static ImmutableList<AccelerationFrameIterator.Frame> FIFTY_NS_EXPECTED_FRAMES = ImmutableList.of(
            new AccelerationFrameIterator.Frame(0, ImmutableList.of(t(0), t(10), t(25), t(50))),
            new AccelerationFrameIterator.Frame(4, ImmutableList.of(t(250), t(251), t(251), t(258), t(300))),
            new AccelerationFrameIterator.Frame(5, ImmutableList.of(t(251), t(251), t(258), t(300), t(301)))
    );
    final Joiner LINE_JOINER = Joiner.on('\n');
    final Joiner TOKEN_JOINER = Joiner.on(", ");

    static Acceleration t(final long nanoTime) {
        return new EqualsingAcceleration(0, 0, 0, nanoTime);
    }

    @Test
    public void simpleTest() {
        final AccelerationFrameIterator i = new AccelerationFrameIterator(50, OBSERVATIONS);
        final List<AccelerationFrameIterator.Frame> observed = new ArrayList<>();
        while (i.hasNext()) {
            observed.add(i.next());
        }
        Assert.assertEquals(observed, FIFTY_NS_EXPECTED_FRAMES, LINE_JOINER.join("", TOKEN_JOINER.join(observed), TOKEN_JOINER.join(FIFTY_NS_EXPECTED_FRAMES)));
    }

    private static final class EqualsingAcceleration extends Acceleration {
        EqualsingAcceleration(final double x, final double y, final double z, final long observationNanoTime) {
            super(x, y, z, observationNanoTime);
        }

        @Override
        public String toString() {
            return Long.toString(this.getNanoTime()) + " ns";
        }

        @SuppressWarnings("EqualsWhichDoesntCheckParameterClass")
        @Override
        public boolean equals(final Object o) {
            return getNanoTime() == ((EqualsingAcceleration) o).getNanoTime();
        }
    }
}
