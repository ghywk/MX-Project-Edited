package kireiko.dev.anticheat.api.events;

import lombok.Getter;
import org.bukkit.util.Vector;

@Getter
public final class SVelocityEvent {
    private final Vector velocity;

    public SVelocityEvent(Vector velocity) {
        this.velocity = velocity;
    }
}
