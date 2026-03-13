package kireiko.dev.anticheat.checks.aim.heuristic;

import kireiko.dev.anticheat.api.data.ConfigLabel;
import kireiko.dev.anticheat.api.events.RotationEvent;
import kireiko.dev.anticheat.checks.aim.AimHeuristicCheck;
import kireiko.dev.anticheat.utils.ConfigCache;
import kireiko.dev.millennium.math.Simplification;
import kireiko.dev.millennium.math.Statistics;

import java.util.Map;
import java.util.TreeMap;

public final class AimConstantCheck implements HeuristicComponent {

    private static final double MODULO_THRESHOLD = 60.0;
    private static final double LINEAR_THRESHOLD = 0.1;
    private static final float MIN_DELTA = 0.1f;
    private static final float MAX_DELTA = 20.0f;

    private final AimHeuristicCheck check;
    private float lastDeltaYaw = 0.0f, lastDeltaPitch = 0.0f;
    private float buffer = 0, buffer2 = 0, buffer3 = 0;
    private int rating = 0, toReview = 0;
    private Map<String, Object> localCfg = new TreeMap<>();

    public AimConstantCheck(final AimHeuristicCheck check) {
        this.check = check;
    }

    @Override
    public ConfigLabel config() {
        localCfg.put("constant(1)_needVl", 8);
        localCfg.put("constant(2)_needVl", 6);
        localCfg.put("constant(3)_needVl", 6);
        return new ConfigLabel("constant_check", localCfg);
    }

    @Override
    public void applyConfig(Map<String, Object> params) {
        localCfg = params;
    }

    @Override
    public void process(final RotationEvent rotationUpdate) {
        if (rotationUpdate.getAbsDelta().getY() == 0 && rotationUpdate.getAbsDelta().getY() == 0) return;
        final float deltaYaw = rotationUpdate.getAbsDelta().getX();
        final float deltaPitch = rotationUpdate.getAbsDelta().getY();

        if (ConfigCache.IGNORE_CINEMATIC || check.getProfile().ignoreCinematic()) return;

        final double sensitivity = check.getProfile().calculateSensitivity();
        final boolean sensitivityTooLow = sensitivity < 50.0 && sensitivity > -1.0
                        || (check.getProfile().getSensitivityProcessor().totalSensitivityClient < 50);
        final double divisorYaw = Statistics.getGcd((long) (deltaYaw * Statistics.EXPANDER), (long) (lastDeltaYaw * Statistics.EXPANDER));
        final double divisorPitch = Statistics.getGcd((long) (deltaPitch * Statistics.EXPANDER), (long) (lastDeltaPitch * Statistics.EXPANDER));

        final double constantYaw = divisorYaw / Statistics.EXPANDER;
        final double constantPitch = divisorPitch / Statistics.EXPANDER;
        { // type 1
            final long expandedPitch = (long) (Statistics.EXPANDER * deltaPitch);
            final long expandedLastPitch = (long) (Statistics.EXPANDER * lastDeltaPitch);
            final long gcd = Statistics.getGcd(expandedPitch, expandedLastPitch);
            final boolean validAngles = deltaYaw > 0.25f && deltaPitch > 0.25f && deltaPitch < MAX_DELTA && deltaYaw < MAX_DELTA;
            final boolean invalid = gcd < 131072L;

            if (invalid && validAngles && !sensitivityTooLow) {
                buffer = Math.min(buffer + 1, 200);
                check.getProfile().debug("&7Aim Constant (1): " + buffer);
                rating++;
                if (buffer > getNumCfg("constant(1)_needVl") + 2) {
                    check.getProfile().punish("Aim", "Heuristic", "Constant rotations (1)", 0.0f);
                    check.getProfile().setAttackBlockToTime(System.currentTimeMillis() + 4000);
                    buffer = 4;
                }
            } else if (buffer > 0) {
                buffer -= 2f;
            }
        }

        { // type 2
            final double currentX = deltaYaw / constantYaw;
            final double currentY = deltaPitch / constantPitch;
            final double previousX = lastDeltaYaw / constantYaw;
            final double previousY = lastDeltaPitch / constantPitch;

            final boolean validDelta = deltaYaw > MIN_DELTA && deltaPitch > MIN_DELTA && deltaYaw < MAX_DELTA && deltaPitch < MAX_DELTA;

            if (validDelta) {
                final double moduloX = currentX % previousX;
                final double moduloY = currentY % previousY;

                final double floorModuloX = Math.abs(Math.floor(moduloX) - moduloX);
                final double floorModuloY = Math.abs(Math.floor(moduloY) - moduloY);

                final boolean invalidX = moduloX > MODULO_THRESHOLD && floorModuloX > LINEAR_THRESHOLD;
                final boolean invalidY = moduloY > MODULO_THRESHOLD && floorModuloY > LINEAR_THRESHOLD;

                if (invalidX && invalidY && !sensitivityTooLow) {
                    buffer2 = Math.min(buffer2 + 1, 200);
                    check.getProfile().debug("&7Aim Constant (2): " + buffer2);
                    rating++;
                    if (buffer2 > getNumCfg("constant(2)_needVl")) {
                        check.getProfile().punish("Aim", "Heuristic", "Constant rotations (2)", 0.0f);
                        check.getProfile().setAttackBlockToTime(System.currentTimeMillis() + 4000);
                        buffer2 = 4;
                    }
                } else if (buffer2 > 0) {
                    buffer2 -= 2f;
                }
            }
        }

        { // type 3
            final double currentX = deltaYaw / constantYaw;
            final double currentY = deltaPitch / constantPitch;
            final double previousX = lastDeltaYaw / constantYaw;
            final double previousY = lastDeltaPitch / constantPitch;

            final boolean validDelta = deltaYaw > MIN_DELTA && deltaPitch > MIN_DELTA && deltaYaw < MAX_DELTA && deltaPitch < MAX_DELTA;

            if (validDelta) {
                final double moduloX = currentX % previousX;
                final double moduloY = currentY % previousY;

                final double floorModuloX = Math.abs(Math.floor(moduloX) - moduloX);
                final double floorModuloY = Math.abs(Math.floor(moduloY) - moduloY);

                final boolean invalidX = moduloX > 60.0 && floorModuloX > 0.1;
                final boolean invalidY = moduloY > 60.0 && floorModuloY > 0.1;

                if (invalidX && invalidY && !sensitivityTooLow) {
                    buffer3 = Math.max(buffer3 + ((deltaPitch < 1 || deltaPitch > 13) ? 2f : 1), 0);
                    check.getProfile().debug("&7Aim Constant (3): " + buffer3 + " " + Simplification.scaleVal(deltaPitch, 3));
                    rating++;
                    final float limit = getNumCfg("constant(3)_needVl") + 1;
                    if (buffer3 > ((check.getProfile().calculateSensitivity() < 70) ? limit + 1 : limit)) {
                        check.getProfile().punish("Aim", "Heuristic", "Constant rotations (3)", 0.0f);
                        check.getProfile().setAttackBlockToTime(System.currentTimeMillis() + 4000);
                        buffer3 = 0;
                    }
                } else if (buffer3 > 0) {
                    buffer3 -= 2f;
                }
            }
        }
        this.lastDeltaYaw = deltaYaw;
        this.lastDeltaPitch = deltaPitch;
    }
    private float getNumCfg(String key) {
        return ((Number) localCfg.get(key)).floatValue();
    }
}
