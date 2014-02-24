package com.github.mccowan.tap;

import com.github.mccowan.timeseries.TimedEntity;

import java.util.Comparator;

class Acceleration implements TimedEntity {
    public static final Comparator<Acceleration> OBSERVATION_TIME_COMPARATOR = new Comparator<Acceleration>() {
        @Override
        public int compare(Acceleration o1, Acceleration o2) {
            return Long.compare(o1.getNanoTime(), o2.getNanoTime());
        }
    };

    public static final Comparator<Acceleration> MAGNITUDE_COMPARATOR = new Comparator<Acceleration>() {
        @Override
        public int compare(Acceleration o1, Acceleration o2) {
            return Double.compare(o1.getMagnitude(), o2.getMagnitude());
        }
    };

    private final double x, y, z, magnitude;
    private final long observationNanoTime;

    Acceleration(final double x, final double y, final double z, final long observationNanoTime) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.magnitude = Math.sqrt(x * x + y * y + z * z);
        this.observationNanoTime = observationNanoTime;
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    public double getZ() {
        return z;
    }

    public long getNanoTime() {
        return this.observationNanoTime;
    }

    public double getMagnitude() {
        return magnitude;
    }

    @Override
    public long getTime() {
        return getNanoTime();
    }
}
