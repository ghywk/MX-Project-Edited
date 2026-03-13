package kireiko.dev.anticheat.api.events;

import kireiko.dev.anticheat.api.player.PlayerProfile;
import kireiko.dev.millennium.vectors.Vec3;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.bukkit.Location;

@Data
@AllArgsConstructor
public final class MoveEvent {
    private PlayerProfile profile;
    private Location from;
    private Location to;

    public Vec3 getDelta() {
        return new Vec3(
                to.getX() - from.getX(),
                to.getY() - from.getY(),
                to.getZ() - from.getZ()
        );
    }
}
