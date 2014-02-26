package com.github.mccowan.timeseries;

import com.google.common.base.Function;
import com.google.common.collect.FluentIterable;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.ArrayList;
import java.util.List;

/**
 * @author mccowan
 */
public class SynchronousTimeSeriesTest {

    static class TestEntity implements TimedEntity {
        final long time;

        TestEntity(long time) {
            this.time = time;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            TestEntity that = (TestEntity) o;

            return time == that.time;

        }

        @Override
        public String toString() {
            return Long.toString(time);
        }

        @Override
        public int hashCode() {
            return (int) (time ^ (time >>> 32));
        }

        @Override
        public long getTime() {
            return time;
        }
    }

    static TestEntity t(final long time) {
        return new TestEntity(time);
    }

    final static List<TestEntity> TEST_DATA = ImmutableList.of(
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

    @Test
    public void addThenIterateTest() {
        final SynchronousTimeSeries<TestEntity> series = new SynchronousTimeSeries<>();
        for (final TestEntity datum : TEST_DATA) {
            series.add(datum);
        }
        final ArrayList<TestEntity> result = Lists.newArrayList(series);
        Assert.assertEquals(result, TEST_DATA);
    }


    final static ImmutableList<ImmutableList<TestEntity>> FIFTY_NS_EXPECTED_FRAMES_CONTENTS = ImmutableList.of(
            ImmutableList.of(t(0), t(10), t(25), t(50)),
            ImmutableList.of(t(250), t(251), t(251), t(258), t(300)),
            ImmutableList.of(t(251), t(251), t(258), t(300), t(301))
    );

    @Test
    public void frameIteratorTest() {
        final SynchronousTimeSeries<TestEntity> series = new SynchronousTimeSeries<>();
        for (final TestEntity datum : TEST_DATA) {
            series.add(datum);
        }

        final List<List<TestEntity>> framesContents = FluentIterable.from(series.frameIterator(50))
                .transform(new Function<TimeSeriesView<TestEntity>, List<TestEntity>>() {
                    public List<TestEntity> apply(final TimeSeriesView<TestEntity> input) {
                        return Lists.newArrayList(input);
                    }
                }).toImmutableList();

        Assert.assertEquals(framesContents, FIFTY_NS_EXPECTED_FRAMES_CONTENTS);
    }
}
