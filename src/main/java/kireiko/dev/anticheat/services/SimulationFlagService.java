package kireiko.dev.anticheat.services;

import kireiko.dev.anticheat.MX;
import kireiko.dev.anticheat.api.player.PlayerProfile;
import kireiko.dev.anticheat.core.AsyncScheduler;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.util.Vector;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArrayList;

public final class SimulationFlagService {

    @Getter
    private static final List<Flag> flags = new CopyOnWriteArrayList<>();

    public static void init() {
        Bukkit.getScheduler().runTaskTimer(MX.getInstance(), () -> {
            AsyncScheduler.run(() -> {
                final Set<Flag> toRemove = new HashSet<>();
                for (Flag flag : flags) {
                    flag.getLocation().add(flag.getVector());
                    if (!isPointWall(flag.getLocation(), 0.3)) {
                        final Location finalLoc = flag.getLocation().clone();
                        Bukkit.getScheduler().runTask(MX.getInstance(), () -> {
                            flag.getProfile().getPlayer().teleport(finalLoc);
                        });
                        flag.setVector(new Vector(
                                flag.vector.getX() * 0.91,
                                flag.vector.getY() - (0.08 * 0.98),
                                flag.vector.getZ() * 0.91));
                    } else toRemove.add(flag);
                }
                flags.removeAll(toRemove);
            });
        }, 0L, 1L);
    }

    private static boolean isPointWall(Location location, final double scale) {
        final double x = location.getX();
        final double y = location.getY() + 0.1;
        final double z = location.getZ();
        for (int dx = -1; dx <= 1; ++dx) {
            for (int dy = -1; dy <= 1; ++dy) {
                for (int dz = -1; dz <= 1; ++dz) {
                    final Material material = new Location(location.getWorld(),
                            x + (double) dx * scale,
                            y + (double) dy * scale,
                            z + (double) dz * scale).getBlock().getType();

                    if (!material.toString().equals("AIR")) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    @Data
    @AllArgsConstructor
    public static class Flag {
        private final PlayerProfile profile;
        private Location location;
        private Vector vector;
    }
}
