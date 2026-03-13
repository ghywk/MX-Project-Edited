package kireiko.dev.anticheat.checks.aim.heuristic;

import kireiko.dev.anticheat.api.data.ConfigLabel;
import kireiko.dev.anticheat.api.events.RotationEvent;
import kireiko.dev.anticheat.api.player.PlayerProfile;
import kireiko.dev.anticheat.checks.aim.AimHeuristicCheck;
import kireiko.dev.millennium.vectors.Vec2f;

import java.util.*;

public final class AimPatternCheck implements HeuristicComponent {
    private final AimHeuristicCheck check;
    private Vec2f oldDelta = new Vec2f(0, 0);
    // private Vec2f oldAbsDelta = new Vec2f(0, 0);
    private final List<Vec2f> sample = new ArrayList<>();
    private static final int PATTERN_LENGTH = 3;
    private static final int SAMPLE_SIZE = 100;
    private static final int MIN_START_INDEX_GAP = PATTERN_LENGTH;
    private float buffer = 0;
    private int longTermRating = 0, toCheck = 0;
    private Map<String, Object> localCfg = new TreeMap<>();

    public AimPatternCheck(final AimHeuristicCheck check) {
        this.check = check;
    }

    @Override
    public ConfigLabel config() {
        localCfg.put("hitCancelTimeMS", 5000);
        localCfg.put("addGlobalVl", 20);
        localCfg.put("buffer", 2.5f);
        localCfg.put("buffer_fade", 0.3f);
        return new ConfigLabel("pattern_check", localCfg);
    }

    @Override
    public void applyConfig(Map<String, Object> params) {
        localCfg = params;
    }

    @Override
    public void process(final RotationEvent event) {
        // if (check.getProfile().ignoreCinematic()) return;
        if (event.getAbsDelta().getY() == 0 && event.getAbsDelta().getY() == 0) return;
        final float
        vlLimit = ((Number) localCfg.get("buffer")).floatValue(),
        vlFade = ((Number) localCfg.get("buffer_fade")).floatValue();
        boolean flagged = false;
        final long blockTime = ((Number) localCfg.get("hitCancelTimeMS")).longValue();
        final float addGlobalVl = ((Number) localCfg.get("addGlobalVl")).floatValue() / 10f;
        if (blockTime <= 0 && addGlobalVl <= 0) return;
        final PlayerProfile profile = check.getProfile();
        final Vec2f delta = event.getDelta();
        // final Vec2f absDelta = event.getAbsDelta();
        final float yawFactor = delta.getX() - oldDelta.getX();
        final float pitchFactor = delta.getY() - oldDelta.getY();
        //profile.getPlayer().sendMessage("yaw: " + yawFactor + " pitch: " + pitchFactor);
        final Vec2f vec = new Vec2f(yawFactor, pitchFactor);
        this.sample.add(vec);
        if (this.sample.size() >= SAMPLE_SIZE) {
            final List<Vec2f> patterns = new ArrayList<>();
            final List<Float>
            rawPatterns = new ArrayList<>(),
            filteredPatterns = new ArrayList<>();
            for (int i = 0; i < SAMPLE_SIZE; i++)
                if (i > 0 && Math.abs(this.sample.get(i).getX()) > 1.0) {
                    rawPatterns.add(Math.abs(this.sample.get(i).getX() - this.sample.get(i - 1).getY()));
                }
            for (final float x : rawPatterns) if (x < 1e-4)
                filteredPatterns.add(x);
            if (filteredPatterns.size() > 3)  {
                flagged = true;
                if (buffer++ >= vlLimit) {
                    profile.punish("Aim", "Pattern", "Suspicious patterns: " + filteredPatterns, addGlobalVl);
                    profile.setAttackBlockToTime(System.currentTimeMillis() + blockTime);
                    buffer -= 1;
                }
            }
            if (!flagged) {
                final int currentSampleSize = this.sample.size();
                // searching pattern
                for (int i = 0; i <= currentSampleSize - PATTERN_LENGTH; ++i) {
                    for (int j = i + MIN_START_INDEX_GAP; j <= currentSampleSize - PATTERN_LENGTH; ++j) {
                        Vec2f pattern = null;
                        for (int k = 0; k < PATTERN_LENGTH; ++k) {
                            final Vec2f first = this.sample.get(i + k);
                            final Vec2f second = this.sample.get(j + k);
                            if (Objects.equals(first, second)) {
                                pattern = first;
                                break;
                            }
                        }
                        if (pattern != null && !patterns.contains(pattern)) patterns.add(pattern);
                    }
                }
                // checking invalid patterns
                longTermRating += patterns.size();
                toCheck++;
                //profile.getPlayer().sendMessage("p: " + patterns);
                if (toCheck >= 8) {
                    //profile.getPlayer().sendMessage("p: " + longTermRating);
                    toCheck = 0;
                    longTermRating = 0;
                }
                if (!patterns.isEmpty()) {
                    profile.debug("&7Aim Patterns: " + patterns);
                }
                for (final Vec2f vec2f : patterns) {
                    final float x = Math.abs(vec2f.getX());
                    final float y = Math.abs(vec2f.getY());
                    if ((x > 1.0 || y > 1.0) && (x > 0.26 && y > 0.26)) {
                        flagged = true;
                        if (buffer++ >= vlLimit) {
                            profile.punish("Aim", "Pattern", "Suspicious pattern: " + vec2f, addGlobalVl);
                            profile.setAttackBlockToTime(System.currentTimeMillis() + blockTime);
                            buffer -= 1f;
                        }
                        break;
                    }
                }
            }
            if (!flagged) buffer = Math.max(0, buffer - vlFade);
            //profile.getPlayer().sendMessage("inv: " + patterns);
            this.sample.clear();
        }
        oldDelta = delta;
        // oldAbsDelta = absDelta;
    }
}