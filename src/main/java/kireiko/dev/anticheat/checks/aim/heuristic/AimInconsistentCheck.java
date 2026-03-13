package kireiko.dev.anticheat.checks.aim.heuristic;

import kireiko.dev.anticheat.api.data.ConfigLabel;
import kireiko.dev.anticheat.api.events.RotationEvent;
import kireiko.dev.anticheat.api.player.PlayerProfile;
import kireiko.dev.anticheat.checks.aim.AimHeuristicCheck;
import kireiko.dev.millennium.math.Statistics;
import kireiko.dev.millennium.vectors.Pair;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public final class AimInconsistentCheck implements HeuristicComponent {
    private final AimHeuristicCheck check;
    private float lastDeltaYaw = 0.0f, lastDeltaPitch = 0.0f;
    private final List<Float> samplesYaw = new ArrayList<>();
    private final List<Float> samplesPitch = new ArrayList<>();
    private Map<String, Object> localCfg = new TreeMap<>();
    private float buffer = 0;


    public AimInconsistentCheck(final AimHeuristicCheck check) {
        this.check = check;
    }

    @Override
    public ConfigLabel config() {
        localCfg.put("hitCancelTimeMS", 4000);
        localCfg.put("addGlobalVl", 0);
        localCfg.put("buffer", 2);
        return new ConfigLabel("inconsistent_check", localCfg);
    }
    @Override
    public void applyConfig(Map<String, Object> params) {
        localCfg = params;
    }

    @Override
    public void process(final RotationEvent rotationUpdate) {
        if (rotationUpdate.getAbsDelta().getY() == 0 && rotationUpdate.getAbsDelta().getY() == 0) return;
        final long cancelTime = ((Number) localCfg.get("hitCancelTimeMS")).longValue();
        if (cancelTime <= 0) return;
        final PlayerProfile profile = check.getProfile();
        final boolean invalidSensitivity =
                        profile.calculateSensitivity() < 75
                        || profile.calculateSensitivity() > 175
                        || profile.getSensitivityProcessor().totalSensitivityClient < 75
                        || profile.getSensitivityProcessor().totalSensitivityClient > 170;
        if (check.getProfile().ignoreCinematic() || invalidSensitivity) return;
        final float deltaYaw = Math.abs(rotationUpdate.getAbsDelta().getX());
        final float deltaPitch = Math.abs(rotationUpdate.getAbsDelta().getY());

        final float differenceYaw = Math.abs(deltaYaw - lastDeltaYaw);
        final float differencePitch = Math.abs(deltaPitch - lastDeltaPitch);

        final float joltX = Math.abs(deltaYaw - differenceYaw);
        final float joltY = Math.abs(deltaPitch - differencePitch);

        samplesYaw.add((float) Statistics.roundToPlace(joltX, 2));
        samplesPitch.add((float) Statistics.roundToPlace(joltY, 2));

        if (samplesYaw.size() + samplesPitch.size() >= 60) {
            if (!(joltX == 0.0 || joltY == 0.0)) {
                final Pair<List<Double>, List<Double>> outliersYaw = Statistics.getOutliers(samplesYaw);
                final Pair<List<Double>, List<Double>> outliersPitch = Statistics.getOutliers(samplesPitch);

                final int duplicatesX = Statistics.getDuplicates(samplesYaw);
                final int duplicatesY = Statistics.getDuplicates(samplesPitch);
                final int duplicatesSum = duplicatesX + duplicatesY;
                final int outliersX = outliersYaw.getX().size() + outliersYaw.getY().size();
                final int outliersY = outliersPitch.getX().size() + outliersPitch.getY().size();
                final float addGlobalVl = ((Number) localCfg.get("addGlobalVl")).floatValue() / 10f;
                final float bufferLimit = ((Number) localCfg.get("buffer")).floatValue();
                check.getProfile().debug("&7Aim Inconsistent: " + outliersX + " "
                                + outliersY + "; duplicates: " + duplicatesX + " " + duplicatesY);
                if ((duplicatesSum <= 3 && outliersX < 10 && outliersY < 7) && buffer++ >= bufferLimit) {
                    check.getProfile().punish("Aim", "Heuristic", "Inconsistent rotations ("
                                    + outliersX + ", " + outliersY + ", duplicates: "
                                    + duplicatesSum + ") [Too low values]", addGlobalVl);
                    check.getProfile().setAttackBlockToTime(System.currentTimeMillis() + cancelTime);
                } else if (((outliersX == 0 || outliersY == 0) && (outliersX > 1 || outliersY > 1)
                                && duplicatesSum <= 3) && buffer++ >= bufferLimit) {
                    check.getProfile().punish("Aim", "Heuristic", "Inconsistent rotations ("
                                    + outliersX + ", " + outliersY + ", duplicates: "
                                    + duplicatesSum + ") [Zero value]", addGlobalVl);
                    check.getProfile().setAttackBlockToTime(System.currentTimeMillis() + cancelTime);
                } else buffer -= 0.5f;
            }
            samplesYaw.clear();
            samplesPitch.clear();
        }

        this.lastDeltaYaw = deltaYaw;
        this.lastDeltaPitch = deltaPitch;
    }
}
