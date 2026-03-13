package kireiko.dev.anticheat.checks.velocity;

import kireiko.dev.anticheat.api.PacketCheckHandler;
import kireiko.dev.anticheat.api.data.ConfigLabel;
import kireiko.dev.anticheat.api.events.CTransactionEvent;
import kireiko.dev.anticheat.api.events.MoveEvent;
import kireiko.dev.anticheat.api.events.SVelocityEvent;
import kireiko.dev.anticheat.api.player.PlayerProfile;
import kireiko.dev.anticheat.managers.CheckManager;
import kireiko.dev.anticheat.services.SimulationFlagService;
import kireiko.dev.anticheat.utils.ConfigCache;
import kireiko.dev.millennium.math.Simplification;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.util.Vector;

import java.util.Map;
import java.util.TreeMap;
import java.util.regex.Pattern;

public final class VelocityCheck implements PacketCheckHandler {
    private static final Pattern pattern = Pattern.compile("(?i)(.*(snow|step|frame|table|water|lava|web|slab|stair|ladder|vine|waterlily|wall|carpet|fence|rod|bed|skull|pot|hopper|door|bars|piston|lily).*)");
    private final PlayerProfile profile;
    private final double[] jumpReset = new double[]{0.248136, 0.3332};
    private float vl = 0, totalVlAtY = 0;
    private long oldTime = System.currentTimeMillis();
    private double mostCloseYMotion = 1.0;
    private int timing = 0;
    private boolean transactionLock = false;
    private Vector velocity = null;
    private Location from = null;
    private boolean isOnGroundFrom = false;
    private Map<String, Object> localCfg = new TreeMap<>();

    @Override
    public ConfigLabel config() {
        localCfg.put("enabled", false);
        localCfg.put("buffer", 6);
        return new ConfigLabel("velocity", localCfg);
    }
    @Override
    public void applyConfig(Map<String, Object> params) {
        localCfg = params;
    }

    @Override
    public Map<String, Object> getConfig() {
        return localCfg;
    }


    public VelocityCheck(PlayerProfile profile) {
        this.profile = profile;
        if (CheckManager.classCheck(this.getClass()))
            this.localCfg = CheckManager.getConfig(this.getClass());
    }

    private static double abs(double v) {
        return Math.abs(v);
    }

    private static double r(double v) {
        return Simplification.scaleVal(v, 6);
    }

    private static boolean ignore(final String block) {
        return pattern.matcher(block).matches();
    }

    @Override
    public void event(Object o) {
        if (!(boolean) localCfg.get("enabled")) return;
        if (o instanceof SVelocityEvent) {
            SVelocityEvent event = (SVelocityEvent) o;
            this.totalVlAtY = 25;
            this.from = profile.getTo().clone();
            this.applyVelocity(event);
            //profile.getPlayer().sendMessage("v; " + event.getVelocity());
        } else if (o instanceof MoveEvent) {
            MoveEvent event = (MoveEvent) o;
            checkVelocity(event);
            this.isOnGroundFrom = profile.isGround();
        } else if (o instanceof CTransactionEvent) {
            transactionLock = false;
        }
    }

    private void applyVelocity(SVelocityEvent event) {
        transactionLock = true;
        Location[] locationsToCheck = {
                this.profile.getTo().clone().add(event.getVelocity()),
                this.profile.getTo().clone().add(event.getVelocity()).add(0, 1, 0)
        };
        boolean allClear = true;
        for (Location loc : locationsToCheck) {
            if (isPointWall(loc, 0.3)) {
                allClear = false;
                break;
            }
        }
        if (allClear) {
            this.velocity = event.getVelocity();
            this.mostCloseYMotion = 1.0;
            this.timing = 0;
        }
        if (vl > 0) vl -= 5;
    }

    private void checkVelocity(MoveEvent event) {
        final long delay = System.currentTimeMillis() - oldTime;
        Location from = event.getFrom();
        Location to = event.getTo();

        if (isPointWall(to.clone().add(0, 1, 0), 0.75)) {
            velocity = null;
        }

        final double x = -(to.getX() - from.getX());
        final double y = -(to.getY() - from.getY());
        final double z = -(to.getZ() - from.getZ());

        //this.protocol.bukkit().sendMessage("m: " + y);
        if (velocity != null) {
            if (abs(abs(y) - abs(velocity.getY())) < mostCloseYMotion)
                mostCloseYMotion = y;
            // time for vertical
            //this.profile.getPlayer().sendMessage("m: " + y);
            if (abs(abs(y) - abs(velocity.getY())) < 0.005) {
                { // time for horizontal
                    double xDiff = x - velocity.getX();
                    double zDiff = z - velocity.getZ();
                    //this.profile.getPlayer().sendMessage("m: " + x + " " + velocity.getX());
                    double total = abs(xDiff) + abs(zDiff);
                    double multi = 1.0;
                    if ((!this.profile.isGround() && isOnGroundFrom)) {
                        // time for horizontal flag
                        if (total > 0.2 * multi) {
                            this.flag("Velocity", "Horizontal", "[Air] " + total, 0.0f, 14);
                        }
                    } else if (total > 0.21 * multi) {
                        this.flag("Velocity", "Horizontal", "[Ground] " + total, 0.0f, 30);
                    }
                }
                //this.protocol.bukkit().sendMessage(x + " " + velocity.getX() + " | " + z + " " + velocity.getZ());
                this.velocity = null;
                this.timing = 0;
            } else if (delay > 25) {
                if (isJumpReset(y)) totalVlAtY = 12;
                if (timing <= 3) {
                    if (!transactionLock) timing++;
                } else {
                    // time for vertical flag
                    if (this.velocity.getY() != 0.003) {
                        this.flag("Velocity", "Vertical", "diff=" + r(Math.abs(velocity.getY() - mostCloseYMotion))
                                + ((totalVlAtY > 12) ? " [Basic]" : " [JumpReset]"), 0.0f, totalVlAtY);
                    }
                    this.velocity = null;
                }
            }
        }
        this.oldTime = System.currentTimeMillis();
    }

    private boolean isPointWall(Location location, final double scale) {
        final double x = location.getX();
        final double y = location.getY() + 0.1;
        final double z = location.getZ();
        for (int dx = -1; dx <= 1; ++dx) {
            for (int dy = -1; dy <= 1; ++dy) {
                for (int dz = -1; dz <= 1; ++dz) {
                    final Material material = new Location(this.profile.getTo().getWorld(),
                            x + (double) dx * scale,
                            y + (double) dy * scale,
                            z + (double) dz * scale).getBlock().getType();

                    if (material.isSolid() || ignore(material.toString().toLowerCase()) || material.toString().contains("GRASS")) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    private void flag(final String check, final String component, final String info, final float m, float vl) {
        this.vl += vl;
        float vlLimit = ((Number) localCfg.get("buffer")).floatValue() * 10f;
        if (this.vl > vlLimit) {
            this.profile.punish(check, component, info, m);
            SimulationFlagService.getFlags().add(new SimulationFlagService.Flag(profile, from, velocity));
            this.vl = vlLimit - 10;
        }
    }

    private boolean isJumpReset(double v) {
        for (double d : this.jumpReset) {
            if (Math.abs(d - v) < 0.005) return true;
        }
        return false;
    }
}
