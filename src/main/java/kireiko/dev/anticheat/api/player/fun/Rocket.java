package kireiko.dev.anticheat.api.player.fun;

import kireiko.dev.anticheat.MX;
import kireiko.dev.anticheat.api.player.PlayerProfile;
import kireiko.dev.anticheat.utils.enums.ParticleTypes;
import kireiko.dev.anticheat.utils.helper.ParticleHelper;
import kireiko.dev.millennium.math.*;
import kireiko.dev.millennium.vectors.Vec2;
import kireiko.dev.millennium.vectors.Vec3;
import lombok.Data;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.util.Vector;

@Data
public final class Rocket implements FunThing {
    private final PlayerProfile linked;
    private final PlayerProfile target;
    private final Location location;
    private double speed;
    private boolean destroyed;
    private boolean optimizer3000;

    public Rocket(final PlayerProfile linked, final PlayerProfile target, final Location location) {
        this.linked = linked;
        this.target = target;
        this.location = location;
        this.location.setWorld(linked.getPlayer().getWorld());
        this.destroyed = false;
        this.speed = 0.25;
        this.optimizer3000 = false;
    }

    @Override
    public void tick() {
        this.optimizer3000 = !optimizer3000;
        final double speed = getSpeed();
        if (this.speed < 0.85) this.speed += 0.005;
        final Vec2 vec2 = Euler.calculateVec2Vec(new Vec3(location.toVector()), new Vec3(target.getTo().toVector()));
        final float yaw = (float) vec2.getX();
        final float pitch = (float) vec2.getY();
        final Vector direction = new Vector(
                -GeneralMath.sin((float) Math.toRadians(yaw), BuildSpeed.FAST),
                -GeneralMath.sin((float) Math.toRadians(pitch), BuildSpeed.FAST),
                GeneralMath.cos((float) Math.toRadians(yaw), BuildSpeed.FAST));
        final double interpolatePitch = 1 - ((Math.abs(pitch) * 1.1111) / 100);
        direction.setX(direction.getX() * interpolatePitch);
        direction.setZ(direction.getZ() * interpolatePitch);
        location.add(direction.multiply(speed));
        { // bound
            final double hitbox = 0.5;
            double x = target.getTo().getX(),
                    y = target.getTo().getY(),
                    z = target.getTo().getZ();
            if (RayTrace.doRayTrace(BuildSpeed.FAST,
                    new Vec2(vec2.getY(), vec2.getX()), new Vec3(location.toVector()),
                    new AxisAlignedBB(
                            x - hitbox, y - 0.1f, z - hitbox,
                            x + hitbox, y + 1.9f, z + hitbox
                    ), 0.85)) {
                { // boom!
                    this.destroyed = true;
                    Bukkit.getScheduler().runTask(MX.getInstance(), () -> {
                        target.getPlayer().damage(100);
                    });
                    ParticleHelper.spawn(location.getWorld(), ParticleTypes.EXPLOSION_LARGE, location, 1, 0, 0, 0, 0);
                }
            }
        }
        if (optimizer3000) { // animation
            ParticleHelper.spawn(location.getWorld(), ParticleTypes.FLAME, location, 1, 0, 0, 0, 0);
            ParticleHelper.spawn(location.getWorld(), ParticleTypes.SMOKE_NORMAL, location, 1, 0, 0, 0, 0);
        }
    }
}
