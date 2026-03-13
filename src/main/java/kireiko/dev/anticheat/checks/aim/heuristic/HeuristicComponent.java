package kireiko.dev.anticheat.checks.aim.heuristic;

import kireiko.dev.anticheat.api.data.ConfigLabel;
import kireiko.dev.anticheat.api.events.RotationEvent;

import java.util.Map;

public interface HeuristicComponent {
    void process(final RotationEvent event);
    ConfigLabel config();
    void applyConfig(Map<String, Object> fileSection);
}
