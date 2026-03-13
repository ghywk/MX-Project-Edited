package kireiko.dev.anticheat.utils;

import org.bukkit.Material;

public final class MaterialUtil {

    public static Material getMaterial(String modernName, String legacyName) {
        try {
            return Material.valueOf(modernName);
        } catch (IllegalArgumentException a) {
            return Material.valueOf(legacyName);
        }
    }
}
