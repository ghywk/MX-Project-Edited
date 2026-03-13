package kireiko.dev.anticheat.utils.math;

import kireiko.dev.millennium.math.FastMath;

public final class VanillaMath implements ClientMath {
    public static float sqrt(float f) {
        return (float) Math.sqrt(f);
    }

    @Override
    public float sin(float value) {
        return FastMath.sin(value);
    }

    @Override
    public float cos(float value) {
        return FastMath.cos(value);
    }
}