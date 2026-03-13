package kireiko.dev.anticheat.api.player.fun;

import kireiko.dev.anticheat.api.player.PlayerProfile;
import kireiko.dev.anticheat.utils.enums.ParticleTypes;
import kireiko.dev.anticheat.utils.helper.ParticleHelper;
import kireiko.dev.millennium.math.BuildSpeed;
import kireiko.dev.millennium.math.Euler;
import kireiko.dev.millennium.math.GeneralMath;
import kireiko.dev.millennium.math.Interpolation;
import kireiko.dev.millennium.vectors.Vec2;
import kireiko.dev.millennium.vectors.Vec3;
import lombok.Data;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.util.Vector;

import static kireiko.dev.anticheat.utils.protocol.ProtocolTools.getBlockAsync;

@Data
public class Hook implements FunThing {
    private final PlayerProfile linked;
    private final Location location;
    private boolean stuck;
    private double yPhys;
    private int hoverTicks;
    private boolean optimizer3000;

    public Hook(final PlayerProfile linked, final Location location) {
        this.linked = linked;
        this.location = location;
        this.location.setWorld(linked.getPlayer().getWorld());
        this.stuck = false;
        this.hoverTicks = 0;
        this.yPhys = 0;
        this.optimizer3000 = false;
    }

    @Override
    public void tick() {
        this.optimizer3000 = !optimizer3000;
        if (!stuck) {
            final double speed = 1.1;
            final float yaw = location.getYaw();
            final float pitch = location.getPitch();
            final Vector direction = new Vector(
                    -GeneralMath.sin((float) Math.toRadians(yaw), BuildSpeed.FAST),
                    -GeneralMath.sin((float) Math.toRadians(pitch), BuildSpeed.FAST),
                    GeneralMath.cos((float) Math.toRadians(yaw), BuildSpeed.FAST));
            final double interpolatePitch = 1 - ((Math.abs(pitch) * 1.1111) / 100);
            direction.setX(direction.getX() * interpolatePitch);
            direction.setZ(direction.getZ() * interpolatePitch);
            location.add(direction.multiply(speed));
            location.add(0, yPhys, 0);
            yPhys -= 0.98e-2;
            { // bound
                double x = location.getX(),
                        y = location.getY(),
                        z = location.getZ();

                for (int dx = -1; dx <= 1; dx++) {
                    for (int dy = -1; dy <= 1; dy++) {
                        for (int dz = -1; dz <= 1; dz++) {
                            final Block block = getBlockAsync(
                                    new Location(
                                            this.linked.getPlayer().getWorld(),
                                            x + (dx * 0.3),
                                            y + (dy * 0.3),
                                            z + (dz * 0.3)
                                    )
                            );
                            if (block == null) continue;
                            final Material material = block.getType();
                            if (!material.toString().contains("AIR")) {
                                stuck = true;
                                break;
                            }
                        }
                    }
                }
            }
        } else {
            if (hoverTicks < 20) {
                hoverTicks++;
                if (linked.getPlayer().getLocation().getWorld().equals(location.getWorld())) {
                    final Vec2 vec2 = Euler.calculateVec2Vec(new Vec3(linked.getTo().toVector()), new Vec3(location.toVector()));
                    final double speed = linked.getPlayer().getLocation().distance(location) / 14d;
                    final double x = -Math.sin(Math.toRadians(vec2.getX())) * speed;
                    final double y = (location.getY() - linked.getTo().getY())
                            / Interpolation.sineInterpolation(30d, 6d, hoverTicks / 19d, Interpolation.Ease.IN);
                    final double z = Math.cos(Math.toRadians(vec2.getX())) * speed;
                    { // motion
                        linked.getPlayer().setVelocity(new Vector(x, y, z));
                    }
                }
            }
        }
        if (hoverTicks < 20 && optimizer3000) { // animation
            for (double d = 0; d < 1.0; d += 0.05) {
                final Location to = linked.getTo();
                final Location i = new Location(
                        linked.getPlayer().getWorld(),
                        Interpolation.sineInterpolation(to.getX(), location.getX(), d, Interpolation.Ease.IN),
                        Interpolation.sineInterpolation(to.getY(), location.getY(), d, Interpolation.Ease.IN),
                        Interpolation.sineInterpolation(to.getZ(), location.getZ(), d, Interpolation.Ease.IN)
                );
                ParticleHelper.spawn(linked.getPlayer().getWorld(), ParticleTypes.CRIT, i, 1, 0, 0, 0, 0);
            }
        }
    }
}
