// ParticleHelper.java
package kireiko.dev.anticheat.utils.helper;

import kireiko.dev.anticheat.utils.ReflectionUtils;
import org.bukkit.Location;
import org.bukkit.World;

import java.lang.reflect.Method;

public final class ParticleHelper {
    private static final boolean MODERN;
    private static final Method LEGACY_METHOD;
    private static final Class<?> PARTICLE_CLASS;

    static {
        PARTICLE_CLASS = ReflectionUtils.getClass("org.bukkit.Particle");
        Method modernMethod = ReflectionUtils.getMethod(World.class, "spawnParticle",
                PARTICLE_CLASS, Location.class, int.class);
        Method legacy = ReflectionUtils.getMethod(World.class, "spawnParticle",
                String.class, Location.class, int.class);

        MODERN = modernMethod != null;
        LEGACY_METHOD = MODERN ? null : legacy;
    }

    public static void spawn(World world, Enum<?> type, Location loc, int count) {
        spawn(world, type, loc, count, 0, 0, 0, 0);
    }

    public static void spawn(World world, Enum<?> type, Location loc, int count,
                             double x, double y, double z, double extra) {
        try {
            if (MODERN) {
                Object[] params = count == 0 ?
                        new Object[]{type, loc, 1} :
                        new Object[]{type, loc, count};
                ReflectionUtils.invokeMethod(world, "spawnParticle", params);
            } else if (LEGACY_METHOD != null) {
                Object[] params = {type.name().toLowerCase(), loc, count, x, y, z, extra};
                LEGACY_METHOD.invoke(world, params);
            }
        } catch (Exception e) {
            handleFallback(world, type, loc, count, x, y, z, extra);
        }
    }

    private static void handleFallback(World world, Enum<?> type, Location loc,
                                       int count, double x, double y, double z, double extra) {
        try {
            if (MODERN) {
                Object[] params = {type, loc, count, x, y, z, extra};
                ReflectionUtils.invokeMethod(world, "spawnParticle", params);
            } else if (LEGACY_METHOD != null) {
                LEGACY_METHOD.invoke(world, type.name().toLowerCase(),
                        loc, count, x, y, z, extra);
            }
        } catch (Exception ignored) {
        }
    }
}