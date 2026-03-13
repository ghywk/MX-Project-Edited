package kireiko.dev.anticheat.api.player.fun;

import kireiko.dev.anticheat.MX;
import kireiko.dev.anticheat.api.data.PlayerContainer;
import kireiko.dev.anticheat.api.player.PlayerProfile;
import kireiko.dev.anticheat.utils.enums.ParticleTypes;
import kireiko.dev.anticheat.utils.helper.ParticleHelper;
import kireiko.dev.millennium.math.AxisAlignedBB;
import kireiko.dev.millennium.math.BuildSpeed;
import kireiko.dev.millennium.math.GeneralMath;
import kireiko.dev.millennium.math.RayTrace;
import kireiko.dev.millennium.vectors.Vec2;
import kireiko.dev.millennium.vectors.Vec3;
import lombok.Data;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.potion.PotionEffect;
import org.bukkit.util.Vector;

import static kireiko.dev.anticheat.utils.protocol.ProtocolTools.getBlockAsync;

@Data
public final class Spell implements FunThing {
    private final PlayerProfile linked;
    private final Location location;
    private final ParticleTypes effect, explosion;
    private double speed, damage;
    private boolean destroyed;
    private boolean optimizer3000;
    private PotionEffect potionEffect;
    private int exist;

    public Spell(final PlayerProfile linked, final Location location,
                 final double speed, final double damage, final ParticleTypes effect,
                 final ParticleTypes explosion, final PotionEffect potionEffect) {
        this.linked = linked;
        this.location = location;
        this.location.setWorld(linked.getPlayer().getWorld());
        this.destroyed = false;
        this.speed = speed;
        this.damage = damage;
        this.effect = effect;
        this.explosion = explosion;
        this.optimizer3000 = false;
        this.potionEffect = potionEffect;
        this.exist = 0;
    }

    @Override
    public void tick() {
        this.optimizer3000 = !optimizer3000;
        final double speed = getSpeed();
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
        this.exist++;
        { // bound player
            final double hitbox = 0.4;
            for (PlayerProfile target : PlayerContainer.getUuidPlayerProfileMap().values()) {
                if (target.getPlayer().getUniqueId().equals(linked.getPlayer().getUniqueId())) continue;
                if (location.getWorld().equals(target.getPlayer().getWorld())
                        && location.distance(target.getPlayer().getLocation()) < 5) {
                    double x = target.getTo().getX(),
                            y = target.getTo().getY(),
                            z = target.getTo().getZ();
                    if (RayTrace.doRayTrace(BuildSpeed.FAST,
                            new Vec2(linked.getTo().getPitch(), linked.getTo().getYaw()), new Vec3(location.toVector()),
                            new AxisAlignedBB(
                                    x - hitbox, y - 0.1f, z - hitbox,
                                    x + hitbox, y + 1.9f, z + hitbox
                            ), speed + 0.4)) {
                        { // boom!
                            this.destroyed = true;
                            Bukkit.getScheduler().runTask(MX.getInstance(), () -> {
                                    Location attackerLoc = linked.getPlayer().getEyeLocation();
                                    Vector attackDirection = attackerLoc.getDirection().normalize();

                                    Vector horizontalKnockback = new Vector(
                                            -attackDirection.getX(),
                                            0,
                                            -attackDirection.getZ()
                                    ).normalize();

                                    double vertical = 0.35 * (1 + (exist / 1200.0));
                                    double horizontal = -0.45;

                                    Vector velocity = horizontalKnockback
                                            .multiply(horizontal)
                                            .setY(vertical);

                                    target.getPlayer().setVelocity(velocity);
                                    target.getPlayer().damage(damage); // XD
                                });
                            ParticleHelper.spawn(location.getWorld(), explosion, location, 1, 0, 0, 0, 0);
                            if (potionEffect != null)
                                Bukkit.getScheduler().runTask(MX.getInstance(),
                                        () -> target.getPlayer().addPotionEffect(potionEffect));
                            break;
                        }
                    }
                }
            }
        }
        { // bound block
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
                            destroyed = true;
                            ParticleHelper.spawn(location.getWorld(), explosion, location, 5);
                            break;
                        }
                    }
                }
            }
        }

        if (optimizer3000) { // animation
            ParticleHelper.spawn(location.getWorld(), effect, location, 1, 0, 0, 0, 0);
        }
        if (exist > 1200) destroyed = true;
    }
}
