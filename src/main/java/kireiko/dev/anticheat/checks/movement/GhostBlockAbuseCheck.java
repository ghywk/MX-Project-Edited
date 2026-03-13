package kireiko.dev.anticheat.checks.movement;

import kireiko.dev.anticheat.MX;
import kireiko.dev.anticheat.api.PacketCheckHandler;
import kireiko.dev.anticheat.api.data.ConfigLabel;
import kireiko.dev.anticheat.api.events.MoveEvent;
import kireiko.dev.anticheat.api.player.PlayerProfile;
import kireiko.dev.anticheat.core.AsyncScheduler;
import kireiko.dev.anticheat.managers.CheckManager;
import kireiko.dev.anticheat.utils.ConfigCache;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;

import java.util.Map;
import java.util.TreeMap;

public class GhostBlockAbuseCheck implements PacketCheckHandler {
    private final PlayerProfile profile;
    private int flags;
    private Location buffLoc;
    private Map<String, Object> localCfg = new TreeMap<>();

    public GhostBlockAbuseCheck(PlayerProfile profile) {
        this.profile = profile;
        this.flags = 0;
        this.buffLoc = null;
        if (CheckManager.classCheck(this.getClass()))
            this.localCfg = CheckManager.getConfig(this.getClass());
    }

    @Override
    public ConfigLabel config() {
        localCfg.put("buffer", 2);
        return new ConfigLabel("ghost_block_abuse", localCfg);
    }
    @Override
    public void applyConfig(Map<String, Object> params) {
        localCfg = params;
    }

    @Override
    public Map<String, Object> getConfig() {
        return localCfg;
    }

    @Override
    public void event(Object o) {
        if (o instanceof MoveEvent) {
            if (!ConfigCache.PREVENT_GHOST_BLOCK_ABUSE || profile.isIgnoreFirstTick()) return;
            MoveEvent event = (MoveEvent) o;
            final Location to = event.getTo();
            final Location from = event.getFrom();
            final int buffer = ((Number) localCfg.get("buffer")).intValue();
            AsyncScheduler.run(() -> {
                final boolean onGround = event.getProfile().isGround();
                if (onGround) {
                    final boolean flag = !(isOnSolidGround(to) || isOnSolidGround(from));
                    if (flag) {
                        this.flags++;
                        profile.debug("&7GhostBlock flags: " + this.flags + "/" + buffer);
                        if (this.flags < buffer) {
                            final Location l = to.clone();
                            l.setWorld(profile.getPlayer().getWorld());
                            this.buffLoc = l;
                        } else {
                            if (this.buffLoc.getWorld().equals(profile.getPlayer().getWorld())
                            ) {
                                if (this.buffLoc.getY() <= to.getY() + 1.0) {
                                    Bukkit.getScheduler().runTask(MX.getInstance(), () -> {
                                        this.profile.getPlayer().teleport(buffLoc);
                                    });
                                }
                                this.flags = 1;
                            } else {
                                this.profile.debug("&7GhostBlock setback invalid world: "
                                                + this.buffLoc.getWorld().getName()
                                                + " " + profile.getPlayer().getWorld().getName());
                                this.flags = 0;
                            }
                        }
                    } else this.flags = 0;
                }
            });
        }
    }
    private boolean isOnSolidGround(final Location location) {
        double x = location.getX(),
                        y = location.getY() - 0.1,
                        z = location.getZ();

        for (int dx = -1; dx <= 1; dx++) {
            for (int dy = -1; dy <= 1; dy++) {
                for (int dz = -1; dz <= 1; dz++) {
                    Block block = getBlockAsync(
                                    new Location(
                                                    this.profile.getPlayer().getWorld(),
                                                    x + (dx * 0.3),
                                                    y + (dy * 0.5),
                                                    z + (dz * 0.3)
                                    )
                    );
                    if (block == null) return true;
                    Material material = block.getType();
                    if (!material.toString().contains("AIR") || material.isSolid()
                                    || ignore(material.toString().toLowerCase())) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    public Block getBlockAsync(final Location location) {
        if (location.getWorld().isChunkLoaded(location.getBlockX() >> 4, location.getBlockZ() >> 4)) {
            return location.getWorld().getBlockAt(location);
        } else {
            return null;
        }
    }
    private static boolean ignore(String block) {
        return block.matches(".*(snow|step|frame|table|slab|stair|ladder|vine|waterlily|wall|carpet|fence|rod|bed|skull|pot|hopper|door|piston|lily).*");
    }
}
