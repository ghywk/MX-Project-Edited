package kireiko.dev.millennium.math;

import kireiko.dev.millennium.vectors.Vec2;
import kireiko.dev.millennium.vectors.Vec2f;
import kireiko.dev.millennium.vectors.Vec3;
import lombok.NonNull;

import static java.lang.Math.PI;

public final class Euler {

    public static boolean compareTwoVectorAngle(@NonNull Vec2 a, @NonNull Vec2 b, double radi) {
        double angleA = Math.atan2(a.getY(), a.getX());
        double angleB = Math.atan2(b.getY(), b.getX());

        double angleDiff = Math.abs(angleA - angleB);

        if (angleDiff > Math.PI) {
            angleDiff = 2 * Math.PI - angleDiff;
        }

        return angleDiff <= Math.toRadians(radi);
    }

    public static double calculateTwoVectorAngleDifference(@NonNull Vec2 a, @NonNull Vec2 b) {
        double angleA = Math.atan2(a.getY(), a.getX());
        double angleB = Math.atan2(b.getY(), b.getX());

        double angleDiff = Math.abs(angleA - angleB);

        if (angleDiff > Math.PI) {
            angleDiff = 2 * Math.PI - angleDiff;
        }
        return angleDiff;
    }

    public static Vec2 calculateVec2Vec(final Vec3 from, final Vec3 to) {
        final Vec3 diff = to.subtract(from);
        final double distance = Math.hypot(diff.xCoord, diff.zCoord);
        final float yaw = (float) (Math.atan2(diff.zCoord, diff.xCoord) * 180.0F / PI) - 90.0F;
        final float pitch = (float) (-(Math.atan2(diff.yCoord, distance) * 180.0F / PI));
        return new Vec2(yaw, pitch);
    }

    public static Vec2 calculateRotationToVec(final Vec3 pos) {
        final float deltaX = (float) pos.xCoord;
        final float deltaY = (float) pos.yCoord;
        final float deltaZ = (float) pos.zCoord;
        final float distance = (float) Math.hypot(deltaX, deltaZ);
        float yaw = (float) (Math.toDegrees(Math.atan2(deltaZ, deltaX)) - 90);
        float pitch = (float) (Math.toDegrees(-Math.atan2(deltaY, distance)));

        return new Vec2(wrapDegrees(yaw), clamp(pitch, -90, 90));
    }

    public static float wrapDegrees(float value) {
        float f = value % 360.0F;

        if (f >= 180.0F) {
            f -= 360.0F;
        }

        if (f < -180.0F) {
            f += 360.0F;
        }

        return f;
    }

    public static float clamp(float num, float min, float max) {
        if (num < min) {
            return min;
        } else {
            return Math.min(num, max);
        }
    }

    public static double getAngleInDegrees(Vec2f delta) {
        double angleInRadians = Math.atan2(delta.getX(), delta.getY());
        double angleInDegrees = Math.toDegrees(angleInRadians);

        if (angleInDegrees < 0) {
            angleInDegrees += 360;
        }

        return angleInDegrees;
    }


    public static double calculateVectorAngle(@NonNull Vec2 a) {
        return Math.atan2(a.getY(), a.getX());
    }

}
