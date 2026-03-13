package kireiko.dev.anticheat.api.events;

import kireiko.dev.anticheat.api.player.PlayerProfile;
import kireiko.dev.millennium.math.Statistics;
import kireiko.dev.millennium.vectors.Vec2f;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public final class RotationEvent {
    private PlayerProfile profile;
    private Vec2f from;
    private Vec2f to;

    private static double absDelta(float v1, float v2) {
        return Math.abs(Math.abs(v1) - Math.abs(v2));
    }

    public double getGCDValue() {
        return Statistics.getGCDValue(profile.getSensitivityProcessor().getWrappedSensitivity());
    }

    public Vec2f getDelta() {
        return new Vec2f(to.getX() - from.getX(), to.getY() - from.getY());
    }

    public Vec2f getAbsDelta() {
        return new Vec2f(
                absDelta(to.getX(), from.getX()),
                absDelta(to.getY(), from.getY())
        );
    }
}
