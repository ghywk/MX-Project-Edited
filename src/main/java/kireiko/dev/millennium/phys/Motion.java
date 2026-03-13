package kireiko.dev.millennium.phys;

import kireiko.dev.millennium.vectors.Vec3;
import lombok.Getter;

@Getter
public final class Motion {

    private final MotionValue x, y, z;

    /**
     * Create an empty constructor if we do not want initial values for our motion.
     */
    public Motion() {
        this.x = new MotionValue(0.0D);
        this.y = new MotionValue(0.0D);
        this.z = new MotionValue(0.0D);
    }

    /**
     * Set an initial value for our base motion.
     */
    public Motion(final double x, final double y, final double z) {
        this.x = new MotionValue(x);
        this.y = new MotionValue(y);
        this.z = new MotionValue(z);
    }

    /**
     * Set an initial value for our base motion.
     */
    public Motion(final MotionValue x, final MotionValue y, final MotionValue z) {
        this.x = new MotionValue(x.get());
        this.y = new MotionValue(y.get());
        this.z = new MotionValue(z.get());
    }

    public void set(final Vec3 vector) {
        this.x.set(vector.xCoord);
        this.y.set(vector.yCoord);
        this.z.set(vector.zCoord);
    }

    public void add(final Vec3 vector) {
        this.x.add(vector.xCoord);
        this.y.add(vector.yCoord);
        this.z.add(vector.zCoord);
    }

    public double distanceSquared(final Motion other) {
        return Math.pow(this.x.get() - other.getX().get(), 2) +
                Math.pow(this.y.get() - other.getY().get(), 2) +
                Math.pow(this.z.get() - other.getZ().get(), 2);
    }

    public double length() {
        return Math.sqrt(this.x.get() * this.x.get()
                + this.y.get() * this.y.get() + this.z.get() * this.z.get());
    }

    public Motion normalize() {
        final double d0 = this.length();
        return d0 < 1.0E-4D ? new Motion(0.0D, 0.0D, 0.0D) : new Motion(this.x.get() / d0, this.y.get() / d0, this.z.get() / d0);
    }

    public Motion clone() {
        return new Motion(x.get(), y.get(), z.get());
    }
}
