package mccowan.tap;

class AccelerationVectorObservation {
    private final double x, y, z;
    private final long observationNanoTime;

    AccelerationVectorObservation(final double x, final double y, final double z, final long observationNanoTime) {
        this.x = x;
        this.y = y;
        this.z = z;
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
        return Math.sqrt(x * x + y * y + z * z);
    }
}
