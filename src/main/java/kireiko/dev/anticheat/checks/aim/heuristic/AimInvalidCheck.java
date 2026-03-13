package kireiko.dev.anticheat.checks.aim.heuristic;

import kireiko.dev.anticheat.api.data.ConfigLabel;
import kireiko.dev.anticheat.api.events.RotationEvent;
import kireiko.dev.anticheat.api.player.PlayerProfile;
import kireiko.dev.anticheat.checks.aim.AimHeuristicCheck;
import kireiko.dev.millennium.math.Statistics;
import kireiko.dev.millennium.vectors.Vec2f;

import java.util.HashMap;
import java.util.Map;

public final class AimInvalidCheck implements HeuristicComponent {
    private final AimHeuristicCheck check;
    private int buffer = 0;
    private static final float INVALID_PITCH = 90f + 1e-6f;
    private Map<String, Object> localCfg = new HashMap<>();
    public AimInvalidCheck(final AimHeuristicCheck check) {
        this.check = check;
    }

    @Override
    public ConfigLabel config() {
        localCfg.put("hitCancelTimeMS", 0);
        localCfg.put("addGlobalVl", 100);
        return new ConfigLabel("invalid_check", localCfg);
    }

    @Override
    public void applyConfig(Map<String, Object> params) {
        localCfg = params;
    }

    @Override
    public void process(final RotationEvent event) {
        //if (check.getProfile().ignoreCinematic()) return;
        if (event.getAbsDelta().getY() == 0 && event.getAbsDelta().getY() == 0) return;
        final PlayerProfile profile = check.getProfile();
        final Vec2f delta = event.getAbsDelta();
        final long blockTime = ((Number) localCfg.get("hitCancelTimeMS")).longValue();
        final float vl = ((Number) localCfg.get("addGlobalVl")).floatValue() / 10f;
        if ((Statistics.isExponentiallySmall(delta.getY())
                && delta.getY() > 0.0
                && delta.getX() > 0.5f)) {
            buffer += 20;
            if (buffer > 70) {
                profile.punish("Aim", "Invalid", "Invalid Pitch " + event.getDelta().getY(), vl);
                profile.setAttackBlockToTime(System.currentTimeMillis() + blockTime);
            }
        } else buffer--;
        if (profile.getTo().getPitch() > INVALID_PITCH) {
            profile.punish("Aim", "Invalid", "Unlimited Pitch " + event.getDelta().getY(), vl);
            profile.setAttackBlockToTime(System.currentTimeMillis() + blockTime);
        }
    }
}